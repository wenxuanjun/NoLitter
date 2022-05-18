package lantian.nolitter.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "forced_mode") val forcedMode: Boolean,
    @ColumnInfo(name = "additional_hooks") val additionalHooks: Boolean
)
