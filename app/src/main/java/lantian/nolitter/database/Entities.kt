package lantian.nolitter.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "package_preference")
data class PackagePreference (
    @PrimaryKey @ColumnInfo(name = "package_name") val packageName: String = "",
    @ColumnInfo(name = "forced_mode") val forcedMode: Boolean = false,
    @ColumnInfo(name = "allow_public_dirs") val allowPublicDirs: Boolean = true,
    @ColumnInfo(name = "additional_hooks") val additionalHooks: Boolean = false,
    @ColumnInfo(name = "redirect_style") val redirectStyle: String = "external"
)
