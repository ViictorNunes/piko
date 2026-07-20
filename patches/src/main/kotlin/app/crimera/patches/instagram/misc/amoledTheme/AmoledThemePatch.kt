/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 §7(b) terms that apply to this code.
 */

package app.crimera.patches.instagram.misc.amoledTheme

import app.crimera.patches.instagram.utils.Constants.COMPATIBILITY_INSTAGRAM
import app.morphe.patcher.patch.resourcePatch
import app.morphe.util.findElementByAttributeValueOrThrow

@Suppress("unused")
val amoledThemePatch =
    resourcePatch(
        name = "Amoled theme",
        description = "Replaces Instagram's dark-mode background greys with pure black for AMOLED displays.",
        default = true,
    ) {
        compatibleWith(COMPATIBILITY_INSTAGRAM)

        execute {
            val nightOverrides =
                mapOf(
                    "igds_secondary_background" to "@color/bds_black",
                    "igds_elevated_background" to "@color/bds_black",
                    "igds_elevated_highlight_background" to "@color/bds_black",
                )

            document("res/values-night/colors.xml").use { document ->
                val colors = document.getElementsByTagName("color")
                nightOverrides.forEach { (name, value) ->
                    colors.findElementByAttributeValueOrThrow("name", name).textContent = value
                }
            }

            val defaultOverrides =
                mapOf(
                    "igds_prism_black" to "#ff000000",
                )

            document("res/values/colors.xml").use { document ->
                val colors = document.getElementsByTagName("color")
                defaultOverrides.forEach { (name, value) ->
                    colors.findElementByAttributeValueOrThrow("name", name).textContent = value
                }
            }
        }
    }
