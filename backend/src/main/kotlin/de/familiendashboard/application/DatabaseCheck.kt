package de.familiendashboard.application

/**
 * Port: Die Anwendung fragt nur "ist die Datenbank erreichbar?".
 * Wie das geprüft wird, entscheidet der Adapter in `infrastructure/`.
 */
interface DatabaseCheck {
    fun isReachable(): Boolean
}
