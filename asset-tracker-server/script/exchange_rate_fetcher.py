import sys
import json
import time
from tradingview_ta import TA_Handler, Interval

def get_usd_krw_from_tv() -> float:
    try:
        handler = TA_Handler(symbol="USDKRW", screener="forex", exchange="FX_IDC", interval=Interval.INTERVAL_1_MINUTE)
        price = handler.get_analysis().indicators["close"]
        return price
    except Exception as e:
        return None

if __name__ == "__main__":
    result = get_usd_krw_from_tv()
    if result:
        response = {
            "success": True,
            "rate": result,
            "timestamp": time.time()
        }
    else:
        response = {
            "success": False,
            "error": "Failed to fetch USD/KRW rate"
        }
    print(json.dumps(response))