FROM stripe/stripe-cli:latest

# Environment variables that will be provided at runtime
ENV STRIPE_API_KEY=""
ENV STRIPE_WEBHOOK_URL="http://backend:9000/api/webhook"

# Create startup script properly
RUN echo '#!/bin/sh' > /start.sh && \
    echo 'if [ -z "$STRIPE_API_KEY" ]; then' >> /start.sh && \
    echo '    echo "Error: STRIPE_API_KEY is not set"' >> /start.sh && \
    echo '    exit 1' >> /start.sh && \
    echo 'fi' >> /start.sh && \
    echo '# Start the webhook listener' >> /start.sh && \
    echo 'stripe listen --api-key $STRIPE_API_KEY --forward-to $STRIPE_WEBHOOK_URL' >> /start.sh && \
    chmod +x /start.sh

# Set the startup script as the entry point
ENTRYPOINT ["/start.sh"]