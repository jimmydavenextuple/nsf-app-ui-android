package com.nextuple.nsf.ui.util

import androidx.compose.ui.Modifier

// This file is intended to house common extension functions that are relevant beyond a single
// package or feature. For any extension functions based on feature/domain-specific models, they
// should be defined as close to the subject as possible for awareness and ease of maintenance.

inline fun Modifier.conditional(
	condition: Boolean,
	onTrue: Modifier.() -> Modifier = { this },
	onFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) then(onTrue(Modifier)) else then(onFalse(Modifier))
