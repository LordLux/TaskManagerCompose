package com.lordlux.composefirst

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.lordlux.composefirst.ui.theme.ComposeFirstTheme

//@Preview(
//	showBackground = true,
//	uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//fun NoteItemPreview() {
//	ComposeFirstTheme {
//		NoteItem(index = 1)
//	}
//}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//	ComposeFirstApp()
//}

@Preview(
	name = "Delete Dark",
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_YES,
	backgroundColor = 0xFF121318
)
@Composable
fun DeleteSwipePreview1() { ComposeFirstTheme { DeleteSwipePreview() } }

@Preview(
	name = "Delete Light",
	showBackground = true,
	uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun DeleteSwipePreview2() { ComposeFirstTheme { DeleteSwipePreview() } }

@Composable
fun DeleteSwipePreview() {
	val error1 = MaterialTheme.colorScheme.error
	val error2 = MaterialTheme.colorScheme.onError
	val bg = MaterialTheme.colorScheme.background
	Column {
		// Inactive
		Box(Modifier.height(90.dp)) {
			BgDeleteBox(
				bgcolor = bg, iconcolor = error1
			)
		}
		// Active
		Box(Modifier.height(90.dp)) {
			BgDeleteBox(
				bgcolor = error1, iconcolor = error2
			)
		}
	}
}