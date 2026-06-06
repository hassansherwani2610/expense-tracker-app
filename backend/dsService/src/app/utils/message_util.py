import re

class MessageUtil:

    def is_bank_sms(self, message):
        message = message.lower()

        # 1. Bank-related keywords
        keywords = ["bank", "account", "card", "transaction", "payment", "withdrawal", "deposit", "balance", "statement", "transfer"]

        # 2. Transaction actions
        actions = ["debited", "credited", "spent", "withdrawn", "paid", "received", "transferred"]

        # 3. Amount pattern (Rs. 500, PKR 1000, etc.)
        amount_pattern = r'(rs\.?|pkr|\$)\s?\d+'

        # 4. Masked card/account (like ****1234)
        masked_pattern = r'(\*{2,}\d{2,4})'

        # CHECKS
        keyword_match = any(word in message for word in keywords)
        actions_match = any(action in message for action in actions)
        amount_match = bool(re.search(amount_pattern, message))
        masked_pattern = bool(re.search(masked_pattern, message))

        checks_sum = sum([keyword_match, actions_match, amount_match, masked_pattern])

        return checks_sum >= 2