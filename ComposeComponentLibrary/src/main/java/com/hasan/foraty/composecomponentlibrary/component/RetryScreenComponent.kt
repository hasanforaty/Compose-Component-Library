package com.hasan.foraty.composecomponentlibrary.component

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput

/**
 *a widget that has a motion base retry , with dragging widget down it will start the Retry Mode
 * @param modifier custom modifier for Box
 * @param isRetrying is widget currently in Retry Mode ?
 * @param onRetry a lambda for when we be in Retry Mode
 * @param content content of Box in this widget
 */
@Composable
fun RetryScreenComponent(
    modifier: Modifier = Modifier,
    isRetrying:Boolean = false,
    onRetry:(Boolean)->Unit,
    content:@Composable BoxScope.(Boolean)->Unit
){


    val progress = remember { mutableStateOf(if (isRetrying)1f else 0.0f) }
    val animatedProgress = animateFloatAsState(
        targetValue = progress.value,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    val inProgress = remember {
        mutableStateOf(isRetrying)
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures{change: PointerInputChange, dragAmount: Float ->
                    change.consumeAllChanges()
                    val drag = dragAmount/100
                    if (!inProgress.value){
                        if ((progress.value + drag)<0){
                            progress.value = 0f
                        }else{
                            progress.value += drag
                        }
                    }

                }
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    Log.d("TAG", "RetryScreenComponent: ${dragAmount.getDistance()}")
                }
            }
            .then(modifier)
    ) {
        if (progress.value>0.0f){
            if (progress.value>0.90f){
                inProgress.value = true
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                onRetry(true)
            }else{
                CircularProgressIndicator(progress = animatedProgress,modifier = Modifier.align(Alignment.Center))
            }

        }
            content(inProgress.value)
    }
}