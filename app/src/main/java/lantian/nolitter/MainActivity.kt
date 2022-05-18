package lantian.nolitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import lantian.nolitter.interfaces.AppUi
import lantian.nolitter.models.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppUi(ViewModelProvider(this)[MainViewModel::class.java]) }
    }
}