package de.erikspall.mensaapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.erikspall.mensaapp.domain.enums.Location
import de.erikspall.mensaapp.domain.enums.StringResEnum

@Composable
fun SettingsRadioDialog(
    showDialog: Boolean = true,
    iconVector: ImageVector,
    title: String,
    message: String,
    items: List<StringResEnum>,
    selectedValue: StringResEnum,
    dismissText: String,
    confirmText: String,
    onConfirm: (StringResEnum) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    //val openDialog = remember { mutableStateOf(true) }
    val selected = remember { mutableStateOf(selectedValue) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                onDismiss()
            },
            icon = { Icon(iconVector, contentDescription = null) },
            title = {
                Text(text = title)
            },
            text = {
                Column {
                    Text(text = message)
                    Divider(
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                    Column {
                        for (item in items) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (selected.value == item),
                                        onClick = { selected.value = item },
                                        role = Role.RadioButton
                                    )
                                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (selected.value == item),
                                    onClick = null // null recommended for accessibility with screenreaders
                                )
                                Text(
                                    modifier = Modifier.padding(start = 16.dp),
                                    text = stringResource(id = item.getValue()),
                                )
                            }
                        }
                    }
                    Divider(
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm(selected.value)
                    }
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(dismissText)
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewSettingsRadioDialog() {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        SettingsRadioDialog(
            showDialog = showDialog.value,
            iconVector = Icons.Rounded.Place,
            title = "Standort wählen",
            confirmText = "Speichern",
            dismissText = "Zurück",
            message = "Wähle hier welchen Standort du sehen möchtest",
            onConfirm = {
                showDialog.value = false
            },
            onDismiss = {
                showDialog.value = false
            },
            items = Location.values().toList(),
            selectedValue = Location.WUERZBURG
        )
    }

}