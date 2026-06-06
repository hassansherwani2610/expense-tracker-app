import os
from langchain_core.prompts import ChatPromptTemplate
from langchain_mistralai import ChatMistralAI
from app.schema.expense import Expense
from dotenv import load_dotenv

class LLMService:
    def __init__(self):
        base_dir = os.path.dirname(os.path.abspath(__file__))
        env_path = os.path.join(base_dir, "..", "..", "..", ".env")
        env_path = os.path.normpath(env_path)

        load_dotenv(dotenv_path=env_path)

        self.api_key = os.getenv("MISTRAL_API_KEY")

        if not self.api_key:
            raise ValueError("MISTRAL_API_KEY not found in environment variables")

        self.prompt = ChatPromptTemplate.from_messages(
            [
                (
                    "system",
                    "You are an expert extraction algorithm. "
                    "Only extract relevant information from the text. "
                    "If you do not know the value of an attribute asked to extract, "
                    "return null for the attribute's value.",
                ),
                ("human", "{text}")
            ]
        )
        self.llm = ChatMistralAI(api_key=self.api_key, model="mistral-large-latest", temperature=0)
        self.runnable = self.prompt | self.llm.with_structured_output(schema=Expense)

    def run_LLM(self, message):
        return self.runnable.invoke({"text": message})