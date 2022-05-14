package lantian.nolitter

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import lantian.nolitter.models.MainViewModel
import lantian.nolitter.ui.AppUi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) Toast.makeText(this, R.string.ui_cleanFolder_failPermission, Toast.LENGTH_SHORT).show()
        }.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        setContent { AppUi(ViewModelProvider(this)[MainViewModel::class.java]) }
    }
}