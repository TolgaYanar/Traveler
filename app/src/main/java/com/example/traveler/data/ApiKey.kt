package com.example.traveler.data

object ApiKey {

    //city
    private var currentCityApiKeyIndex = 0
    private val cityApiKeys = listOf(
        "AcSgjUaoDOyfo+zxZSfyLQ==Gpc49F8jYu0GSiU1",
        "I76m8UjXZC4T1S2qEBIeig==gDb5W0aGY0rzQ9ys",
        "niKcZF9Jy4rFEsthV1oRDA==SZnlzL6YPJngMstV",
        "4JW72fW5knYfaNDkraw56A==opg6dNVlLplVQ8rR"
    )

    fun getCityApiKey(): String {
        return cityApiKeys[currentCityApiKeyIndex]
    }

    fun rotateCityApiKey() {
        currentCityApiKeyIndex = (currentCityApiKeyIndex + 1) % cityApiKeys.size
    }

    private val weatherApiKey = "16f3d29a19e372f77ae7cdce77df685a"

    fun getWeatherApiKey(): String {
        return weatherApiKey
    }

    private val touristApiKey = "29d783588ad44be8963bd1789aef538f"

    fun getTouristApiKey(): String {
        return touristApiKey
    }

    private val imageApiKey = "8mA6ag3hncORGS8uahovbiZPMbVMGNg9NOMURz4IZinZXQ6HijZXTD5U"

    fun getImageApiKey(): String {
        return imageApiKey
    }
}