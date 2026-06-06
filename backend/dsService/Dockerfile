FROM python:3.11-slim

WORKDIR /app

# Copy distribution
COPY dist/ds_service-1.0.tar.gz .

# Upgrade pip (important)
# RUN pip install --upgrade pip

# Install your package
RUN pip install --no-cache-dir ds_service-1.0.tar.gz

# Flask config
ENV FLASK_APP=app

# Expose port
EXPOSE 8010

# Run app
CMD ["flask", "run", "--host=0.0.0.0", "--port=8010"]