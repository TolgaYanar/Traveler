package com.example.traveler.data

object ApiKey {

    //city
    private var currentCityApiKeyIndex = 0
    private val cityApiKeys = listOf(
        "D2JZhlY55CFocn5rf3FmoxvzxtncQYQHxKiQ1S6E",
        "AcSgjUaoDOyfo+zxZSfyLQ==Gpc49F8jYu0GSiU1",
        "I76m8UjXZC4T1S2qEBIeig==gDb5W0aGY0rzQ9ys",
        "niKcZF9Jy4rFEsthV1oRDA==SZnlzL6YPJngMstV",
        "4JW72fW5knYfaNDkraw56A==opg6dNVlLplVQ8rR",
        "pZWA35GbN/Yq5+Ii3pK5tg==zWS4i5wATE2gwhZB",
        "M1KDKptomGB2etKkZ63pbw==hyfHxCDzWlOWjGxF",
        "fB7Xh5ae1Wl0tx7zlAya4Q==GnF15hj9gN6Vm7sH"
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