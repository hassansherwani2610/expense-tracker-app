from app.utils.message_util import MessageUtil
from app.services.llm_service import LLMService

class MessageService:
    def __init__(self):
        self.message_util = MessageUtil()

    def process_message(self, message):

        if not self.message_util.is_bank_sms(message):
            return None

        llm_service = LLMService()

        return llm_service.run_LLM(message)