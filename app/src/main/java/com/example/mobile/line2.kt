////package com.example.mobile
////
////import androidx.compose.foundation.Canvas
////import androidx.compose.foundation.Image
////import androidx.compose.foundation.layout.*
////import androidx.compose.foundation.rememberScrollState
////import androidx.compose.foundation.verticalScroll
////import androidx.compose.runtime.Composable
////import androidx.compose.runtime.remember
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.graphics.Color
////import androidx.compose.ui.graphics.Path
////import androidx.compose.ui.graphics.drawscope.Stroke
////import androidx.compose.ui.res.colorResource
////import androidx.compose.ui.res.painterResource
////import androidx.compose.ui.unit.dp
////
////@Composable
////fun MoodLineGraph(wellnessData: List<WellnessEntry>) {
////
////    val daysOfWeek = listOf(
////        "Sun", "Mon", "Tues", "Weds", "Thurs", "Fri", "Satur"
////    )
//
////    val moods = listOf(
////        Mood("Joyful", R.drawable.veryhappy, colorResource(R.color.lightblue)),
////        Mood("Happy", R.drawable.sentiment_satisfied_24dp_b89230_fill0_wght400_grad0_opsz24,
////            colorResource(R.color.green)
////        ),
////        Mood("Meh", R.drawable.neutral, colorResource(R.color.yellow)),
////        Mood("Bad", R.drawable.sentiment_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,
////            colorResource(R.color.orange)
////        ),
////        Mood("Down", R.drawable.sentiment_very_dissatisfied_24dp_b89230_fill0_wght400_grad0_opsz24,
////            colorResource(R.color.red)
////        )
////    )
//
//    // Create a path to draw the lines
//    val path = remember { Path() }
//
//    // Dimensions for the graph
//    val graphWidth = 300.dp
//    val graphHeight = 200.dp
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Canvas(
//            modifier = Modifier
//                .size(graphWidth, graphHeight)
//        ) {
//            // Plot each data point and connect them with lines
//            wellnessData.forEachIndexed { index, entry ->
//                val dayIndex = daysOfWeek.indexOf(entry.date) // Find x-axis position based on day
//                val moodPosition = moodPositions.find { it.first == entry.mood }?.second ?: 0f
//
//                val xPos = (dayIndex.toFloat() / (daysOfWeek.size - 1)) * size.width
//                val yPos = size.height - (moodPosition / (moodPositions.size - 1)) * size.height
//
//                if (index == 0) {
//                    path.moveTo(xPos, yPos)
//                } else {
//                    path.lineTo(xPos, yPos)
//                }
//
//                // Draw mood icon at the plotted point
//                val moodIcon = iconMap[entry.mood]
//                if (moodIcon != null) {
//                    drawImage(
//                        painter = painterResource(id = moodIcon),
//                        topLeft = androidx.compose.ui.geometry.Offset(xPos - 16.dp.toPx(), yPos - 16.dp.toPx()),
//                        size = androidx.compose.ui.geometry.Size(32.dp.toPx(), 32.dp.toPx())
//                    )
//                }
//            }
//
//            // Draw the line connecting the points
//            drawPath(
//                path = path,
//                color = Color.Blue,
//                style = Stroke(width = 4f)
//            )
//        }
//    }
//}
//
