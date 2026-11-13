package com.example.weatherdiscussion.ui.feed

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.weatherdiscussion.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FeedScreen(viewModel: FeedViewModel = viewModel(factory = FeedViewModel.factory)){

    val context = LocalContext.current

    GlideImage(
        model = null,
        contentDescription = "",
        modifier = Modifier.size(160.dp),
        loading = placeholder(R.drawable.weather_placeholder),
    ){
        it.thumbnail(Glide.with(context).load("URL"))
    }

}