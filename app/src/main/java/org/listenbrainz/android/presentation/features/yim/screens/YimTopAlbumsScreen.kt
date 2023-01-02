package org.listenbrainz.android.presentation.features.yim.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.GlideLazyListPreloader
import kotlinx.coroutines.delay
import org.listenbrainz.android.R
import org.listenbrainz.android.data.sources.api.entities.yimdata.TopRecording
import org.listenbrainz.android.presentation.features.yim.YimViewModel
import org.listenbrainz.android.presentation.features.yim.screens.components.YimShareButton
import org.listenbrainz.android.presentation.features.yim.ui.theme.LocalYimPaddings
import org.listenbrainz.android.presentation.features.yim.ui.theme.YearInMusicTheme
import org.listenbrainz.android.presentation.features.yim.ui.theme.YimPaddings

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun YimTopAlbumsScreen(
    yimViewModel: YimViewModel,
    navController: NavHostController,
    paddings: YimPaddings = LocalYimPaddings.current,
) {
    YearInMusicTheme(redTheme = false) {
        
        var startAnim by remember{
            mutableStateOf(false)
        }
        
        val cardHeight by animateDpAsState(
            targetValue = if (startAnim) 460.dp else 50.dp,
            animationSpec = tween(durationMillis = 1000, delayMillis = 1000)
        )
    
        LaunchedEffect(Unit) {
            startAnim = true
        }
    
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elevated Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
                    .padding(horizontal = paddings.DefaultPadding),
                shadowElevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Card Heading
                    Text(
                        text = "Top Albums of 2022",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddings.smallPadding)
                    )
                
                    // Main Heading
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = paddings.tinyPadding)
                            .background(MaterialTheme.colorScheme.secondary),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        
                        /** Variables for glide preloader*/
                        val uriList : ArrayList<String> = arrayListOf()
                        val topRecordings : List<TopRecording>? = yimViewModel.getTopRecordings()?.toList()
                        topRecordings?.forEach { item ->
                            // https://archive.org/download/mbid-{caa_release_mbid}/mbid-{caa_release_mbid}-{caa_id}_thumb500.jpg
                            uriList.add("https://archive.org/download/mbid-${item.caaReleaseMbid}/mbid-${item.caaReleaseMbid}-${item.caaId}_thumb500.jpg")
                            // TODO: Decide whether to use 500 or 250 as image quality.
                        }
                        
                        // Pre-loading images
                        val listState = rememberLazyListState()
                        
                        GlideLazyListPreloader(
                            state = listState,
                            data = uriList,
                            size = Size(300f,300f),
                            numberOfItemsToPreload = 20,
                            fixedVisibleItemCount = 3
                        ){ item, requestBuilder ->
                            requestBuilder.load(item)
                        }
                        
                        AlbumViewer(list = topRecordings, listState = listState)
                        
                    }
                }
                
            }
            
            
            // To Avoid multiple recompositions
            var animateShareButton by remember { mutableStateOf(false) }
            LaunchedEffect(Unit){
                delay(2700)
                animateShareButton = true
            }
    
            // Share Button
            AnimatedVisibility(visible = animateShareButton) {
                YimShareButton(isRedTheme = false, modifier = Modifier.absoluteOffset(y = 50.dp))
            }
            
        }
        // End of Highest Column
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumViewer(list: List<TopRecording>?, listState: LazyListState) {
    
    // This prevents image from being blur or crashing the app.
    var renderImage by remember { mutableStateOf(false) }
    LaunchedEffect(true){
        delay(2000)
        renderImage = true
    }
    
    // Avoids pop in
    val alphaAnimation by animateFloatAsState(
        targetValue = if (renderImage) 1f else 0f,
        animationSpec = tween(1000)
    )
    
    // This if condition avoids stuttering as it blocks composition until rest of the animations are finished.
    if (renderImage) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                // Animates the grayish background of this window.
                .padding(vertical = LocalYimPaddings.current.extraLargePadding)
                .alpha(alphaAnimation)
                .animateContentSize(),
        ) {
            
            items(list!!.toList()) { item ->
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    
                    GlideImage(
                        model = "https://archive.org/download/mbid-${item.caaReleaseMbid}/mbid-${item.caaReleaseMbid}-${item.caaId}_thumb500.jpg",
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentDescription = null
                    ) {
                        it.placeholder(R.drawable.ic_metabrainz_logo_no_text).thumbnail()
                        // TODO: Decide placeholder
                    }
                    
                    // Track name
                    Text(
                        text = item.trackName,
                        modifier = Modifier.padding(top = 5.dp),
                        color = Color(0xFF39296F),
                        fontFamily = FontFamily(Font(R.font.roboto_bold))
                    )
                    
                    // Artist text
                    Text(
                        text = item.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF727272)
                    )
                    
                }
                
                Spacer(modifier = Modifier.width(LocalYimPaddings.current.DefaultPadding))
                
            }
        }
    }
}

/** Album Viewer Using Coil (Here for future if decision shakes)*/
/*
@Composable
fun CoilAlbumViewer(list: List<TopRecording>?) {
    
    // This prevents image from being blur or crashing the app.
   var renderImage by remember { mutableStateOf(false) }
    LaunchedEffect(true){
        delay(2100)      // TODO: Test baseline with different internet speeds.
        renderImage = true
    }
    
    val listState = rememberLazyListState()
    
    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            // Animates the grayish background of this window.
            .padding(
                vertical = if (renderImage)
                    LocalYimPaddings.current.extraLargePadding
                else
                    0.dp
            )
            .animateContentSize(),
    ) {
        if (list != null) {
            items(list.toList()) { item ->
                // https://archive.org/download/mbid-{caa_release_mbid}/mbid-{caa_release_mbid}-{caa_id}_thumb500.jpg
                val imagePainter = if (item.caaId != null) {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://archive.org/download/mbid-${item.caaReleaseMbid}/mbid-${item.caaReleaseMbid}-${item.caaId}_thumb250.jpg")
                            .crossfade(true)
                            .placeholder(R.drawable.ic_metabrainz_logo_no_text)
                            .build(),
                        filterQuality = FilterQuality.Medium,
                        error = painterResource(id = R.drawable.ic_metabrainz_logo_no_text),
                    )
                }else {
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ic_album)
                            .build()
                    )
                }
                
               if (renderImage) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Image(
                            painter = imagePainter,
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        Text(text = item.trackName)
                    }
                    Spacer(modifier = Modifier.width(LocalYimPaddings.current.DefaultPadding))
               }
               
            }
        }
    }
}*/

