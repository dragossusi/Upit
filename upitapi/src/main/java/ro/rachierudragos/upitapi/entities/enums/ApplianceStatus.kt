package ro.rachierudragos.upitapi.entities.enums

import com.google.gson.annotations.SerializedName

/**
 * Upit
 *
 * @author Dragos
 * @since 30.06.2019
 */
enum class ApplianceStatus {
    @SerializedName("Pending")
    PENDING,
    @SerializedName("Approved")
    APPROVED,
    @SerializedName("Rejected")
    REJECTED
}