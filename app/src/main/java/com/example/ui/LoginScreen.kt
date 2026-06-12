package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val backgroundNavy = Color(0xFF0A1F38).copy(alpha = 0.6f)
    val fieldBackground = Color(0xFFE0E2E6)
    val textDark = Color(0xFF333333)
    val textGray = Color(0xFF666666)
    val astColor = Color(0xFFD32F2F)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundNavy
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AILogo(modifier = Modifier.padding(bottom = 60.dp))
            
            val textFieldColors = TextFieldDefaults.colors(
                focusedContainerColor = fieldBackground,
                unfocusedContainerColor = fieldBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = textDark,
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                focusedLabelColor = textGray,
                unfocusedLabelColor = textGray,
                focusedLeadingIconColor = textGray,
                unfocusedLeadingIconColor = textGray
            )
            
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { 
                    Row {
                        Text("Usuario / Correo ", color = textGray)
                        Text("*", color = astColor)
                    }
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors
            )
            
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { 
                    Row {
                        Text("Contraseña ", color = textGray)
                        Text("*", color = astColor)
                    }
                },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = "Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors
            )
            
            ElevatedButton(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = fieldBackground,
                    contentColor = backgroundNavy
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "Acceder",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AILogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    logoSize: androidx.compose.ui.unit.Dp = 120.dp,
    textSize: androidx.compose.ui.unit.TextUnit = 28.sp,
    spacerSize: androidx.compose.ui.unit.Dp = 16.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.size(logoSize)) {
            val w = size.width
            val h = size.height

            val leftBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF4550), Color(0xFFFF4081)),
                start = Offset(0f, 0f),
                end = Offset(w / 2, h)
            )
            
            val rightBrush = Brush.linearGradient(
                colors = listOf(Color(0xFF00B4DB), Color(0xFF0083B0), Color(0xFF673AB7)),
                start = Offset(w / 2, 0f),
                end = Offset(w, h)
            )

            // Left Shape 
            val leftPath = Path().apply {
                moveTo(w * 0.1f, h * 0.2f)
                lineTo(w * 0.45f, h * 0.1f)
                lineTo(w * 0.45f, h * 0.9f)
                lineTo(w * 0.1f, h * 0.75f)
                close()
            }
            drawPath(path = leftPath, brush = leftBrush)

            // Right Shape
            val rightPath = Path().apply {
                moveTo(w * 0.55f, h * 0.1f)
                lineTo(w * 0.9f, h * 0.2f)
                lineTo(w * 0.9f, h * 0.75f)
                lineTo(w * 0.55f, h * 0.9f)
                lineTo(w * 0.55f, h * 0.65f)
                lineTo(w * 0.75f, h * 0.55f)
                lineTo(w * 0.75f, h * 0.4f)
                lineTo(w * 0.55f, h * 0.35f)
                close()
            }
            drawPath(path = rightPath, brush = rightBrush)
        }
        
        if (showText) {
            Spacer(modifier = Modifier.height(spacerSize))
            
            Text(
                text = "AI INVENTARIO",
                style = TextStyle(
                    fontSize = textSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF8A80), Color(0xFF8C9EFF))
                    )
                )
            )
        }
    }
}