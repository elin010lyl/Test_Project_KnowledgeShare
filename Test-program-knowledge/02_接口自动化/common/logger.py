# -*- coding: utf-8 -*-
"""统一日志：控制台 + logs/run.log 双输出"""
import logging
import os
import sys


def get_logger(name: str = "api_test") -> logging.Logger:
    logger = logging.getLogger(name)
    if logger.handlers:  # 避免重复添加 handler
        return logger
    logger.setLevel(logging.INFO)
    fmt = logging.Formatter("%(asctime)s [%(levelname)s] %(message)s", "%H:%M:%S")

    sh = logging.StreamHandler(sys.stdout)
    sh.setFormatter(fmt)
    logger.addHandler(sh)

    os.makedirs("logs", exist_ok=True)
    fh = logging.FileHandler(os.path.join("logs", "run.log"), encoding="utf-8")
    fh.setFormatter(fmt)
    logger.addHandler(fh)
    return logger


logger = get_logger()
