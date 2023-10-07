package net.j2i.networkinfo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.j2i.networkinfo.ui.theme.NetworkInfoTheme
import java.lang.StringBuilder
import java.net.NetworkInterface
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateListOf

class MainActivity : ComponentActivity() {

    object SampleData {
        val messageList = listOf(
            AddressEntry("home", "127.0.0.1"),
            AddressEntry("fake gateway", "192.168.0.1")
        )
    }


    val  _addressList =mutableStateListOf<AddressEntry>()
    val addressList:List<AddressEntry> =  _addressList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RefreshNetworkInformation()

        setContent {
            NetworkInfoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NetworkInfoComposable(_addressList, this)
                }
            }
        }
    }

    fun RefreshNetworkInformation() {
        val updatedAddressList = LoadNetworkInterfaces()
        _addressList.clear()
        _addressList.addAll(updatedAddressList)
    }

    @ExperimentalUnsignedTypes
    fun ByteArray.toHex2(): String = asUByteArray().joinToString(":") { it.toString(radix = 16).padStart(2, '0') }

    fun LoadNetworkInterfaces():List<AddressEntry>
    {
        val addressEntryList:ArrayList<AddressEntry> = ArrayList<AddressEntry>();
        val interfaceList = NetworkInterface.getNetworkInterfaces()
        if(interfaceList != null) {
            for (networkInterface in interfaceList) {
                val name = networkInterface.name
                val inetAddressList = networkInterface.inetAddresses
                for (inetAddress in inetAddressList) {
                    val address =
                        AddressEntry(name, inetAddress.toString())
                    addressEntryList.add(address)
                }
            }
        }
        return addressEntryList
    }


}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode")
@Composable
fun NetworkAddressPreview()
{
    NetworkInfoTheme {
        NetworkInfoComposable( MainActivity.SampleData.messageList)
    }
}

@Composable
fun NetworkInfoComposable(addressList:List<AddressEntry>, activity:MainActivity? = null) {
    Column {
        Row {
            Spacer(Modifier.weight(1f))
            Button(onClick = {
                activity?.RefreshNetworkInformation()
            }) {
                Text("Refresh")
            }
        }
        LazyColumn {

            items(addressList) { address ->

                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        contentDescription = "Data on network adapter " + address.interfaceName,
                        painter = painterResource(R.drawable.computer_96),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = address.interfaceName)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(address.inetAddress)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

            }
        }
    }
}