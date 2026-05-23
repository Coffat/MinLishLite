package com.example.minlishlite.core.result

class WordNotFoundException : Exception("Word not found in dictionary")

class NetworkException(cause: Throwable? = null) : Exception("Network error", cause)
