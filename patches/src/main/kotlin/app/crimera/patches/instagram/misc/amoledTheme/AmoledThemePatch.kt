/*
 * Copyright (C) 2026 piko <https://github.com/crimera/piko>
 *
 * See the included NOTICE file for GPLv3 §7(b) terms that apply to this code.
 */

package app.crimera.patches.instagram.misc.amoledTheme

import app.crimera.patches.instagram.utils.Constants.COMPATIBILITY_INSTAGRAM
import app.morphe.patcher.patch.resourcePatch
import app.morphe.util.findElementByAttributeValueOrThrow
import java.io.File

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

            // Thanks to the Instafel project for this improvement
            val smaliFile = getProjectDir().walkTopDown()
                .firstOrNull { it.isFile && it.name == "BasePrismColorsV2.smali" }

            if (smaliFile == null) {
                println("BasePrismColorsV2.smali not found")
            } else if (patchGray1600(smaliFile)) {
                println("GRAY_1600 patched -> 0xff000000L")
            } else {
                println("GRAY_1600 not patched")
            }
        }
    }

private fun getProjectDir(): File {
    val path = System.getProperty("morphe.projectDir")
        ?: error("Unable to resolve project directory for smali scan")
    return File(path)
}

private fun patchGray1600(file: File): Boolean {
    val lines = file.readLines().toMutableList()

    for (i in lines.indices) {
        val line = lines[i].trim()
        if (!line.startsWith("sput-wide") || !line.contains("->GRAY_1600:J")) continue

        for (j in i - 1 downTo maxOf(0, i - 20)) {
            val prev = lines[j].trim()

            if (prev.startsWith("const-wide")) {
                val register = prev.substringAfter(' ').substringBefore(',').trim()
                val indent = lines[j].takeWhile { it == ' ' || it == '\t' }
                lines[j] = "${indent}const-wide $register, 0xff000000L"
                file.writeText(lines.joinToString("\n"))
                return true
            }

            if (prev.startsWith("sput-wide") || prev.startsWith(".method")) {
                break
            }
        }
    }

    return false
}
