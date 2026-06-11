from typing import Optional
from pydantic import BaseModel, Field


class Expense(BaseModel):
    """Information about a transaction made on any Card"""

    amount: Optional[str] = Field(
        default=None,
        title="expense",
        description="Expense made on the transaction"
    )

    merchant: Optional[str] = Field(
        default=None,
        title="merchant",
        description="Merchant name whom the transaction has been made"
    )

    currency: Optional[str] = Field(
        default=None,
        title="currency",
        description="Currency of the transaction"
    )