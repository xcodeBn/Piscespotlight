package io.piscesbn.xcodebn.piscespotlight

import io.piscesbn.xcodebn.piscespotlight.spotlight.SpotlightTarget

/**
 * Demo spotlight targets for PDF Summarizer app
 * These represent the different features we want to highlight in the tutorial
 */

// Main features
data object UploadButtonTarget : SpotlightTarget
data object SummarizeButtonTarget : SpotlightTarget
data object SaveButtonTarget : SpotlightTarget

// Toolbar features
data object HighlightToolTarget : SpotlightTarget
data object AnnotateToolTarget : SpotlightTarget
data object ExportToolTarget : SpotlightTarget

// Settings
data object SettingsButtonTarget : SpotlightTarget
data object LanguageSelectorTarget : SpotlightTarget

// Advanced features
data object AIAssistantTarget : SpotlightTarget
data object ShareButtonTarget : SpotlightTarget
