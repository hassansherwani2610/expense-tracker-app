import requests

class AuthService:

    def __init__(self):
        self.auth_url = "http://authservice:9898/auth/v1/validate"

    def validate_token(self, token):

        headers = {
            "Authorization": f"Bearer {token}"
        }

        response = requests.get(
            self.auth_url,
            headers=headers,
            timeout=10
        )

        response.raise_for_status()

        return response.json()