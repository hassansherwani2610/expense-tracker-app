import os
import json

from flask import Flask
from flask import request, jsonify

from kafka import KafkaProducer

from app.services.message_service import MessageService
from app.services.auth_service import AuthService


def create_app():

    app = Flask(__name__)
    app.config.from_pyfile('config.py')

    message_service = MessageService()
    auth_service = AuthService()

    kafka_host = os.getenv('KAFKA_HOST', 'localhost')
    kafka_port = os.getenv('KAFKA_PORT', '9092')

    kafka_bootstrap_servers = f"{kafka_host}:{kafka_port}"

    print("Kafka server is " + kafka_bootstrap_servers)

    kafka_producer = KafkaProducer(
        bootstrap_servers=kafka_bootstrap_servers,
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

    @app.route('/v1/ds/message', methods=['POST'])
    def handle_message():

        try:

            auth_header = request.headers.get("Authorization")

            if not auth_header:
                return jsonify({
                    "error": "Authorization header is missing"
                }), 401

            if not auth_header.startswith("Bearer "):
                return jsonify({
                    "error": "Invalid Authorization header"
                }), 401

            token = auth_header.replace("Bearer ", "")

            user = auth_service.validate_token(token)

            user_id = user.get("userId")

            if not user_id:
                return jsonify({
                    "error": "UserId not found in token"
                }), 401

            received_message = request.json.get("message")

            result = message_service.process_message(
                received_message
            )

            if result is None:
                return jsonify({
                    "error": "Not a bank sms"
                }), 400

            data = result.model_dump()

            data["user_id"] = user_id

            print("KAFKA PAYLOAD =", data)

            kafka_producer.send(
                "expense_service",
                value=data
            )

            kafka_producer.flush()

            return jsonify(data)

        except Exception as e:

            import traceback
            traceback.print_exc()

            return jsonify({
                "error": str(e)
            }), 500

    @app.route('/v1/ds/ping', methods=['GET'])
    def handle_get():
        return 'Pong'

    return app


# This is what Flask's FLASK_APP=app picks up automatically
app = create_app()