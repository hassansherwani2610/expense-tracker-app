from app.utils.message_util import MessageUtil
from app.services.llm_service import LLMService

class MessageService:
    def __init__(self):
        self.message_util = MessageUtil()
        self.llm_service = LLMService()

    def process_message(self, message):
        if self.message_util.is_bank_sms(message):
            return self.llm_service.run_LLM(message)
        else:
            return None