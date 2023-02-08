package com.vitorpamplona.amethyst.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.vitorpamplona.amethyst.R
import com.vitorpamplona.amethyst.lnurl.LightningAddressResolver

@Composable
fun InvoiceRequest(lud16: String, onClose: () -> Unit ) {
  val context = LocalContext.current

  Column(modifier = Modifier
    .fillMaxWidth()
    .padding(start = 30.dp, end = 30.dp)
    .clip(shape = RoundedCornerShape(10.dp))
    .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), RoundedCornerShape(15.dp))
  ) {

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp)
    ) {

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp)
      ) {
        Icon(
          painter = painterResource(R.drawable.lightning),
          null,
          modifier = Modifier.size(20.dp),
          tint = Color.Unspecified
        )

        Text(
          text = "Lightning Invoice",
          fontSize = 20.sp,
          fontWeight = FontWeight.W500,
          modifier = Modifier.padding(start = 10.dp)
        )
      }

      Divider()

      var message by remember { mutableStateOf("") }
      var amount by remember { mutableStateOf(1000L) }

      OutlinedTextField(
        label = { Text(text = "Note to Receiver") },
        modifier = Modifier.fillMaxWidth(),
        value = message,
        onValueChange = { message = it },
        placeholder = {
          Text(
            text = "Thank you so much!",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
          )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
          capitalization = KeyboardCapitalization.Sentences
        ),
        singleLine = true
      )

      OutlinedTextField(
        label = { Text(text = "Amount in Sats") },
        modifier = Modifier.fillMaxWidth(),
        value = amount.toString(),
        onValueChange = { amount = it.toLong() },
        placeholder = {
          Text(
            text = "1000",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
          )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
          keyboardType = KeyboardType.Number
        ),
        singleLine = true
      )

      Button(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        onClick = {
          LightningAddressResolver().lnAddressInvoice(lud16, amount * 1000, message) {
            runCatching {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse("lightning:$it"))
              startActivity(context, intent, null)
            }
            onClose()
          }
        },
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = MaterialTheme.colors.primary
        )
      ) {
        Text(text = "Send", color = Color.White, fontSize = 20.sp)
      }
    }
  }
}