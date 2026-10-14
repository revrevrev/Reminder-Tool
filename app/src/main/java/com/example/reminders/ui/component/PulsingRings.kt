package com.example.reminders.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PulsingRings(ringColor: Color, discColor: Color, iconColor: Color, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "pulse")

    val scale1 by transition.animateFloat(
        initialValue = 0.6f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ), label = "scale1"
    )
    val alpha1 by transition.animateFloat(
        initialValue = 0.7f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ), label = "alpha1"
    )
    val scale2 by transition.animateFloat(
        initialValue = 0.6f, targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 1000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ), label = "scale2"
    )
    val alpha2 by transition.animateFloat(
        initialValue = 0.7f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 1000, easing = EaseOut),
            repeatMode = RepeatMode.Restart
        ), label = "alpha2"
    )
    val wiggle by transition.animateFloat(
        initialValue = 0f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 0; (-14f) at 100; 12f at 250; (-8f) at 400; 5f at 550; 0f at 700
            },
            repeatMode = RepeatMode.Restart
        ), label = "wiggle"
    )

    Box(modifier = modifier.size(190.dp), contentAlignment = Alignment.Center) {
        // Ring 1
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale1)
                .clip(CircleShape)
                .background(ringColor.copy(alpha = alpha1))
        )
        // Ring 2
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale2)
                .clip(CircleShape)
                .background(ringColor.copy(alpha = alpha2))
        )
        // White disc with wiggling bell
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(discColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = iconColor
            )
        }
    }
}
