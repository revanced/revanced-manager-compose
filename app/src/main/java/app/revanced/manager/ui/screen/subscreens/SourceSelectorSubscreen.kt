package app.revanced.manager.ui.screen.subscreens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.revanced.manager.R
import app.revanced.manager.ui.component.AppLargeTopBar
import app.revanced.manager.ui.component.AppScaffold
import app.revanced.manager.ui.component.SourceItem
import app.revanced.manager.ui.viewmodel.SourceSelectorViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSelectorSubscreen(
    onBackClick: () -> Unit,
    viewModel: SourceSelectorViewModel = getViewModel()
) {
    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            viewModel.loadBundle(uri)
            onBackClick()
            return@rememberLauncherForActivityResult
        }
        Toast.makeText(context, "Couldn't load local patch bundle.", Toast.LENGTH_SHORT).show()
    }
    AppScaffold(
        topBar = { scrollBehavior ->
            AppLargeTopBar(
                topBarTitle = stringResource(R.string.select_sources),
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO */ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            ListItem(
                modifier = Modifier
                    .clickable { filePicker.launch(arrayOf("application/java-archive")) },
                headlineText = { Text(stringResource(R.string.select_bundle_from_storage)) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.uwu),
                        contentDescription = null
                    )
                }
            )
            Divider()
            SourceItem()
            SourceItem()
            SourceItem()
        }
    }
}