package com.aerosync.bank_link_sdk

enum class EnvironmentType(val value: String) {
    STAGE("https://staging-sync.aero.inc"),
    SANDBOX("https://sandbox.aerosync.com"),
    PROD("https://sync.aero.inc")
}