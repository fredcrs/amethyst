package com.vitorpamplona.amethyst.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.vitorpamplona.amethyst.ui.actions.CloseButton
import com.vitorpamplona.amethyst.ui.actions.SaveToGallery

@Composable
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
fun ZoomableImageView(word: String) {
  val clipboardManager = LocalClipboardManager.current

  // store the dialog open or close state
  var dialogOpen by remember {
    mutableStateOf(false)
  }

  AsyncImage(
    model = word,
    contentDescription = word,
    contentScale = ContentScale.FillWidth,
    modifier = Modifier
      .padding(top = 4.dp)
      .fillMaxWidth()
      .clip(shape = RoundedCornerShape(15.dp))
      .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), RoundedCornerShape(15.dp))
      .combinedClickable(
        onClick = { dialogOpen = true },
        onLongClick = { clipboardManager.setText(AnnotatedString(word)) },
      )
  )

  if (dialogOpen) {
    Dialog(
      onDismissRequest = { dialogOpen = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
          modifier = Modifier.padding(10.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            CloseButton(onCancel = {
              dialogOpen = false
            })

            SaveToGallery(url = word)
          }

          ZoomableAsyncImage(word)
        }
      }
    }
  }
}