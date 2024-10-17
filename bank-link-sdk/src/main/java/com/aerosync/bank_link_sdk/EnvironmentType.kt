package com.aerosync.bank_link_sdk

enum class EnvironmentType(val value: String) {
    DEV("https://dev.aerosync.com"),
    STAGE("https://staging.aerosync.com"),
    SANDBOX("https://sandbox.aerosync.com"),
    PROD("https://www.aerosync.com")
}