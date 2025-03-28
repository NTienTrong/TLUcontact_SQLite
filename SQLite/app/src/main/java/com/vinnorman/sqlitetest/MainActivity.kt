package com.vinnorman.sqlitetest

import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import com.vinnorman.sqlitetest.ui.theme.SQLiteTestTheme

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DBHelper(this)

        setContent {
            SQLiteTestTheme {
                MainScreen(dbHelper)
            }
        }
    }
}

@Composable
fun MainScreen(dbHelper: DBHelper) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf(emptyList<Contact>()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var showContacts by remember { mutableStateOf(false) } // Thêm state để kiểm soát hiển thị danh sách

    fun refreshContacts() {
        contacts = getContacts(dbHelper)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        addContact(dbHelper, name, phone)
                        name = ""
                        phone = ""
                        Toast.makeText(dbHelper.context, "Thêm thành công", Toast.LENGTH_SHORT).show() // Báo thêm thành công
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Thêm")
                }
                Button(
                    onClick = {
                        refreshContacts()
                        showContacts = true // Hiển thị danh sách khi nhấn "Hiển thị"
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Hiển thị")
                }
            }

            if (showContacts) { // Chỉ hiển thị danh sách nếu showContacts là true
                LazyColumn {
                    items(contacts) { contact ->
                        Text(
                            text = "Tên: ${contact.name}, SĐT: ${contact.phone}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedContact = contact
                                    editName = contact.name
                                    editPhone = contact.phone
                                    showDialog = true
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chỉnh sửa/Xóa") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedContact?.let {
                            updateContact(dbHelper, it.name, editName, editPhone)
                            refreshContacts()
                            showDialog = false
                        }
                    }
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedContact?.let {
                            deleteContact(dbHelper, it.name)
                            refreshContacts()
                            showDialog = false
                        }
                    }
                ) {
                    Text("Xóa")
                }
            }
        )
    }
}

data class Contact(val name: String, val phone: String)

fun getContacts(dbHelper: DBHelper): List<Contact> {
    val cursor = dbHelper.getAllContacts()
    val contacts = mutableListOf<Contact>()
    if (cursor.moveToFirst()) {
        do {
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val phone = cursor.getString(cursor.getColumnIndex("phone"))
            contacts.add(Contact(name, phone))
        } while (cursor.moveToNext())
    }
    return contacts
}

fun addContact(dbHelper: DBHelper, name: String, phone: String) {
    val result = dbHelper.addContact(name, phone)
}

fun updateContact(dbHelper: DBHelper, oldName: String, name: String, phone: String) {
    val result = dbHelper.updateContact(oldName, name, phone)
    if (result > 0) {
        Toast.makeText(dbHelper.context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(dbHelper.context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
    }
}

fun deleteContact(dbHelper: DBHelper, name: String) {
    val result = dbHelper.deleteContact(name)
    if (result > 0) {
        Toast.makeText(dbHelper.context, "Xóa thành công", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(dbHelper.context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
    }
}