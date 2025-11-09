package io.piscesbn.xcodebn.piscespotlight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.piscesbn.xcodebn.piscespotlight.spotlight.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Demo App: PDF Summarizer with Spotlight Tutorial
 * Tests real-world usage of PiscesSpotlight library
 */
@Composable
@Preview
fun App() {
    MaterialTheme {
        PDFSummarizerApp()
    }
}

@Composable
fun PDFSummarizerApp() {
    val tutorialState = rememberPiscesTutorialState()
    val scope = rememberCoroutineScope()

    // Main onboarding tutorial
    val tutorials = remember {
        listOf(
            PiscesTutorialConfig(
                id = "onboarding",
                steps = listOf(
                    SpotlightStep(
                        UploadButtonTarget,
                        "Upload PDF",
                        "Start by uploading a PDF document you want to summarize",
                        TooltipPosition.Top
                    ),
                    SpotlightStep(
                        SummarizeButtonTarget,
                        "Summarize",
                        "Click here to generate an AI-powered summary of your document",
                        TooltipPosition.Bottom
                    ),
                    SpotlightStep(
                        HighlightToolTarget,
                        "Highlight Tool",
                        "Use this to highlight important sections in your document",
                        TooltipPosition.Top
                    ),
                    SpotlightStep(
                        AnnotateToolTarget,
                        "Annotate Tool",
                        "Add notes and comments to your PDF",
                        TooltipPosition.Top
                    ),
                    SpotlightStep(
                        ExportToolTarget,
                        "Export",
                        "Export your document with annotations",
                        TooltipPosition.Top
                    ),
                    SpotlightStep(
                        SaveButtonTarget,
                        "Save",
                        "Save your work and annotations",
                        TooltipPosition.Top
                    ),
                    SpotlightStep(
                        AIAssistantTarget,
                        "AI Assistant",
                        "Chat with AI about your document content",
                        TooltipPosition.Left
                    ),
                    SpotlightStep(
                        ShareButtonTarget,
                        "Share",
                        "Share your summary with others",
                        TooltipPosition.Left
                    )
                )
            )
        )
    }

    PiscesSpotlightContainer(
        state = tutorialState,
        tutorials = tutorials,
        onTutorialComplete = { tutorialId ->
            println("Tutorial completed: $tutorialId")
        }
    ) {
        PDFSummarizerContent(
            onRestartTutorial = {
                scope.launch {
                    tutorialState.reset()
                    tutorials.firstOrNull()?.let { tutorialState.startTutorial(it.id) }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PDFSummarizerContent(
    onRestartTutorial: () -> Unit = {}
) {
    var documentLoaded by remember { mutableStateOf(false) }
    var showSettingsMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF Summarizer") },
                actions = {
                    Box {
                        IconButton(
                            onClick = { showSettingsMenu = true },
                            modifier = Modifier.piscesSpotlightTarget(SettingsButtonTarget)
                        ) {
                            Icon(TablerIcons.Settings, "Settings")
                        }

                        DropdownMenu(
                            expanded = showSettingsMenu,
                            onDismissRequest = { showSettingsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Restart Tutorial") },
                                onClick = {
                                    showSettingsMenu = false
                                    onRestartTutorial()
                                },
                                leadingIcon = {
                                    Icon(TablerIcons.Refresh, "Restart")
                                }
                            )
                            Divider()
                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = {
                                    showSettingsMenu = false
                                },
                                leadingIcon = {
                                    Icon(TablerIcons.InfoCircle, "About")
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { documentLoaded = true },
                    modifier = Modifier
                        .weight(1f)
                        .piscesSpotlightTarget(UploadButtonTarget)
                ) {
                    Icon(TablerIcons.Upload, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Upload PDF")
                }

                Button(
                    onClick = { },
                    enabled = documentLoaded,
                    modifier = Modifier
                        .weight(1f)
                        .piscesSpotlightTarget(SummarizeButtonTarget)
                ) {
                    Icon(TablerIcons.Wand, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Summarize")
                }
            }

            // Document viewer area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (documentLoaded) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                TablerIcons.FileText,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "sample_document.pdf",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Ready for summarization",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            "No document loaded",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Toolbar
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ToolButton(
                        icon = TablerIcons.Pencil,
                        label = "Highlight",
                        modifier = Modifier.piscesSpotlightTarget(HighlightToolTarget)
                    )
                    ToolButton(
                        icon = TablerIcons.Edit,
                        label = "Annotate",
                        modifier = Modifier.piscesSpotlightTarget(AnnotateToolTarget)
                    )
                    ToolButton(
                        icon = TablerIcons.Download,
                        label = "Export",
                        modifier = Modifier.piscesSpotlightTarget(ExportToolTarget)
                    )
                    ToolButton(
                        icon = TablerIcons.DeviceFloppy,
                        label = "Save",
                        modifier = Modifier.piscesSpotlightTarget(SaveButtonTarget)
                    )
                }
            }

            // Side features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .piscesSpotlightTarget(AIAssistantTarget)
                ) {
                    Icon(TablerIcons.MessageCircle, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("AI Chat")
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .piscesSpotlightTarget(ShareButtonTarget)
                ) {
                    Icon(TablerIcons.Share, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Share")
                }
            }

            // Restart tutorial button
            TextButton(
                onClick = onRestartTutorial,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(TablerIcons.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Restart Tutorial")
            }
        }
    }
}

@Composable
fun ToolButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(onClick = { }) {
            Icon(icon, label)
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}