package com.example.pdtranslator.ui.theme

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.pdtranslator.R
import java.util.Calendar
import kotlin.math.sin

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Zone palettes — sourced from MLPD level sources,
// Window.java, CharSprite.java, FogOfWar.java,
// mob sprites, boss encounters, particle effects
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
data class ZonePalette(
  val color1: Color, val color2: Color,
  val wall: Color, val wallLight: Color, val wallDark: Color,
  val floor: Color, val mortar: Color
)

// 24 hourly zone palettes — each hour has a unique dungeon atmosphere
// Sourced from real MLPD color values across all chapters, bosses, and special levels

// ── 0:00 恶魔大厅深层 Deep Demon Halls ──
// HallsLevel color1=0x801500, color2=0xA68521, FistSprite.Dark blood=0x4A2F53
private val HOUR_00 = ZonePalette(
  Color(0xFF801500), Color(0xFFA68521),
  Color(0xFF3D1A0E), Color(0xFF5A2814), Color(0xFF280E06),
  Color(0xFF140A04), Color(0xFF0E0604)
)

// ── 1:00 燃烧拳魔 Burning Fist ──
// FistSprite.Burning blood=0xFFDD34, Halls lava red
private val HOUR_01 = ZonePalette(
  Color(0xFFFFDD34), Color(0xFFEE7722),
  Color(0xFF4A2200), Color(0xFF6B3300), Color(0xFF2E1500),
  Color(0xFF180C02), Color(0xFF100800)
)

// ── 2:00 腐烂拳魔 Rotting Fist ──
// FistSprite.Rotting blood=0xB8BBA1, organic decay
private val HOUR_02 = ZonePalette(
  Color(0xFFB8BBA1), Color(0xFF7F5424),
  Color(0xFF3A3828), Color(0xFF504C38), Color(0xFF24221A),
  Color(0xFF141310), Color(0xFF0C0B08)
)

// ── 3:00 冰霜拳魔 Ice Fist ──
// FistSprite.Ice blood=0x26CCC2, HaloFist=0x34C9C9
private val HOUR_03 = ZonePalette(
  Color(0xFF26CCC2), Color(0xFF34C9C9),
  Color(0xFF1A3838), Color(0xFF2A5050), Color(0xFF0E2424),
  Color(0xFF0A1818), Color(0xFF061010)
)

// ── 4:00 下水道入口 Sewers Entrance ──
// SewerLevel color1=0x48763C, color2=0x59994A
private val HOUR_04 = ZonePalette(
  Color(0xFF48763C), Color(0xFF59994A),
  Color(0xFF2A4428), Color(0xFF3A5A35), Color(0xFF1A2E18),
  Color(0xFF0E1A0C), Color(0xFF0A120A)
)

// ── 5:00 下水道深处 Deep Sewers (Goo) ──
// GooSprite blood=0x000000, toxic green particles=0x50FF60
private val HOUR_05 = ZonePalette(
  Color(0xFF50FF60), Color(0xFF22BB55),
  Color(0xFF1E3820), Color(0xFF2E4E30), Color(0xFF122416),
  Color(0xFF0C180E), Color(0xFF080E08)
)

// ── 6:00 花园 Garden Level ──
// LeafParticle 0x004400..0x88CC44, garden greens
private val HOUR_06 = ZonePalette(
  Color(0xFF88CC44), Color(0xFF66BB6A),
  Color(0xFF2E4420), Color(0xFF3E5A30), Color(0xFF1C2E14),
  Color(0xFF101C0C), Color(0xFF0A140A)
)

// ── 7:00 监狱入口 Prison Entrance ──
// PrisonLevel color1=0x6A723D, color2=0x88924C
private val HOUR_07 = ZonePalette(
  Color(0xFF6A723D), Color(0xFF88924C),
  Color(0xFF3D3F26), Color(0xFF555838), Color(0xFF282A18),
  Color(0xFF141510), Color(0xFF0C0D0A)
)

// ── 8:00 监狱牢房 Prison Cells ──
// PrisonLevel torch=0xFFFFCC, lantern atmosphere
private val HOUR_08 = ZonePalette(
  Color(0xFFFFFFCC), Color(0xFFDDAA22),
  Color(0xFF44422A), Color(0xFF5C5A3A), Color(0xFF2E2C1C),
  Color(0xFF18170E), Color(0xFF100F0A)
)

// ── 9:00 天狗BOSS Tengu Boss ──
// Tengu title=0xFF0000, DangerIndicator=0xC03838
private val HOUR_09 = ZonePalette(
  Color(0xFFC03838), Color(0xFFFF0000),
  Color(0xFF3E2020), Color(0xFF583030), Color(0xFF281414),
  Color(0xFF180C0C), Color(0xFF100808)
)

// ── 10:00 洞穴入口 Caves Entrance ──
// CavesLevel color1=0x534F3E, color2=0xB9D661
private val HOUR_10 = ZonePalette(
  Color(0xFF534F3E), Color(0xFFB9D661),
  Color(0xFF3A3628), Color(0xFF504A38), Color(0xFF24221A),
  Color(0xFF12110C), Color(0xFF0A0A08)
)

// ── 11:00 矿洞深处 Deep Mines ──
// DM300 blood=0xFFFF88, mining orange, crystal wisps=0x66B3FF
private val HOUR_11 = ZonePalette(
  Color(0xFFFFFF88), Color(0xFF66B3FF),
  Color(0xFF3A3420), Color(0xFF504830), Color(0xFF241E14),
  Color(0xFF14100A), Color(0xFF0C0A06)
)

// ── 12:00 水晶洞窟 Crystal Caves ──
// CrystalWispSprite blood=0x66B3FF/0x2EE62E, STORM=0x8AD8D8
private val HOUR_12 = ZonePalette(
  Color(0xFF66B3FF), Color(0xFF8AD8D8),
  Color(0xFF2A3040), Color(0xFF3A4458), Color(0xFF1A2030),
  Color(0xFF0E1420), Color(0xFF080E18)
)

// ── 13:00 矮人城入口 Dwarf City Entrance ──
// CityLevel color1=0x4B6636, color2=0xF2F2F2
private val HOUR_13 = ZonePalette(
  Color(0xFF4B6636), Color(0xFFF2F2F2),
  Color(0xFF3A3A3A), Color(0xFF505050), Color(0xFF222222),
  Color(0xFF141414), Color(0xFF0A0A0A)
)

// ── 14:00 矮人城市中心 Dwarf City Center ──
// City steel, LootIndicator=0x185898, machinery
private val HOUR_14 = ZonePalette(
  Color(0xFF185898), Color(0xFFB0BEC5),
  Color(0xFF2E3A44), Color(0xFF44525C), Color(0xFF1C2630),
  Color(0xFF101820), Color(0xFF0A1018)
)

// ── 15:00 矮人国王 Dwarf King Boss ──
// WardSprite blood=0xCC33FF, GolemSprite blood=0x80706C
private val HOUR_15 = ZonePalette(
  Color(0xFFCC33FF), Color(0xFF80706C),
  Color(0xFF342838), Color(0xFF4A3A50), Color(0xFF201A24),
  Color(0xFF141018), Color(0xFF0C0A10)
)

// ── 16:00 恶魔大厅入口 Demon Halls Entrance ──
// Halls base red glow, GDX_COLOR=0xE44D3C
private val HOUR_16 = ZonePalette(
  Color(0xFFE44D3C), Color(0xFFA68521),
  Color(0xFF3D1A0E), Color(0xFF5A2814), Color(0xFF280E06),
  Color(0xFF140A04), Color(0xFF0E0604)
)

// ── 17:00 魔眼层 Evil Eyes ──
// DeepPK_COLOR=0x792F9E, dark magic purple
private val HOUR_17 = ZonePalette(
  Color(0xFF792F9E), Color(0xFFFF1493),
  Color(0xFF2E1838), Color(0xFF442650), Color(0xFF1A0E24),
  Color(0xFF100A18), Color(0xFF0A0610)
)

// ── 18:00 蝎子巢穴 Scorpio Nest ──
// PoisonParticle 0x00FF00..0x8844FF, toxic + arcane
private val HOUR_18 = ZonePalette(
  Color(0xFF00FF00), Color(0xFF8844FF),
  Color(0xFF1E2E18), Color(0xFF2E4426), Color(0xFF121C0E),
  Color(0xFF0C140A), Color(0xFF080E06)
)

// ── 19:00 犹格索托斯 Yog-Dzewa Boss ──
// Yog-Zot title=0xFF0000, flash=0xE44D3C, hellfire
private val HOUR_19 = ZonePalette(
  Color(0xFFFF0000), Color(0xFFE44D3C),
  Color(0xFF440000), Color(0xFF661000), Color(0xFF2A0000),
  Color(0xFF1A0000), Color(0xFF100000)
)

// ── 20:00 明亮拳魔 Bright Fist ──
// FistSprite.Bright blood=0xFFFFFF, holy light
private val HOUR_20 = ZonePalette(
  Color(0xFFFFFFFF), Color(0xFFFFFFAA),
  Color(0xFF3A3828), Color(0xFF504C38), Color(0xFF242218),
  Color(0xFF161410), Color(0xFF0E0C0A)
)

// ── 21:00 暗影拳魔 Dark Fist ──
// FistSprite.Dark blood=0x4A2F53, shadow magic
private val HOUR_21 = ZonePalette(
  Color(0xFF4A2F53), Color(0xFFB085D5),
  Color(0xFF221828), Color(0xFF34263C), Color(0xFF140E18),
  Color(0xFF0C0810), Color(0xFF080608)
)

// ── 22:00 锈蚀拳魔 Rusted Fist ──
// FistSprite.Rusted blood=0x7F7F7F, CORROSION 0xAAAAAA→0xFF8800
private val HOUR_22 = ZonePalette(
  Color(0xFFAAAAAA), Color(0xFFFF8800),
  Color(0xFF363636), Color(0xFF4A4A4A), Color(0xFF222222),
  Color(0xFF141414), Color(0xFF0C0C0C)
)

// ── 23:00 最终之厅 Last Level ──
// NewLastLevel halos=0xE44D3C, gold TEXT_WIN=0xFFFF88
private val HOUR_23 = ZonePalette(
  Color(0xFFFFFF88), Color(0xFFE44D3C),
  Color(0xFF3D2010), Color(0xFF5A3018), Color(0xFF281408),
  Color(0xFF180C04), Color(0xFF100804)
)

// 24 hourly zones
private val HOURLY_ZONES = listOf(
  HOUR_00, HOUR_01, HOUR_02, HOUR_03, HOUR_04, HOUR_05,
  HOUR_06, HOUR_07, HOUR_08, HOUR_09, HOUR_10, HOUR_11,
  HOUR_12, HOUR_13, HOUR_14, HOUR_15, HOUR_16, HOUR_17,
  HOUR_18, HOUR_19, HOUR_20, HOUR_21, HOUR_22, HOUR_23
)

/** Current hour index 0..23 */
fun getTimeSegment(): Pair<Int, Float> {
  val cal = Calendar.getInstance()
  val hour = cal.get(Calendar.HOUR_OF_DAY)
  val minute = cal.get(Calendar.MINUTE)
  return Pair(hour, minute / 60f)
}

/**
 * Composable state that ticks every 60s, triggering recomposition.
 * Returns current totalMinutes as an observable int.
 */
@Composable
fun rememberTimeTick(): Int {
  val cal = Calendar.getInstance()
  var tick by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(60_000L)
      val now = Calendar.getInstance()
      tick = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    }
  }
  return tick
}

/** Direct zone palette — each hour has its own distinct look */
fun currentZonePalette(): ZonePalette {
  val (hour, _) = getTimeSegment()
  return HOURLY_ZONES[hour]
}

data class TimeTint(val accent1: Color, val accent2: Color, val surfaceTint: Color)
fun currentTimeTint(): TimeTint {
  val p = currentZonePalette()
  return TimeTint(p.color1, p.color2, p.floor)
}

/** Zone names — each hour maps to a dungeon area/encounter */
fun currentZoneName(): String {
  return when (getTimeSegment().first) {
    0  -> "恶魔大厅深层 Deep Demon Halls"
    1  -> "燃烧拳魔 Burning Fist"
    2  -> "腐烂拳魔 Rotting Fist"
    3  -> "冰霜拳魔 Ice Fist"
    4  -> "下水道 Sewers"
    5  -> "下水道BOSS Goo"
    6  -> "花园 Garden"
    7  -> "监狱 Prison"
    8  -> "监狱牢房 Prison Cells"
    9  -> "天狗 Tengu"
    10 -> "洞穴 Caves"
    11 -> "矿洞深处 Deep Mines"
    12 -> "水晶洞窟 Crystal Caves"
    13 -> "矮人城 Dwarf City"
    14 -> "矮人城中心 City Center"
    15 -> "矮人国王 Dwarf King"
    16 -> "恶魔大厅 Demon Halls"
    17 -> "魔眼层 Evil Eyes"
    18 -> "蝎子巢穴 Scorpio Nest"
    19 -> "犹格索托斯 Yog-Dzewa"
    20 -> "光明拳魔 Bright Fist"
    21 -> "暗影拳魔 Dark Fist"
    22 -> "锈蚀拳魔 Rusted Fist"
    23 -> "最终之厅 Last Level"
    else -> "地牢 Dungeon"
  }
}

/** Bottom nav icon res IDs that change with time of day */
fun pdNavIcons(): Triple<Int, Int, Int> {
  return when (getTimeSegment().first) {
    // Demon Halls / Boss hours: scroll + sword + potion (classic adventure)
    0, 1, 2, 3, 16, 19 -> Triple(R.drawable.ic_pd_scroll, R.drawable.ic_pd_sword, R.drawable.ic_pd_potion)
    // Sewers / Garden: chest + key + potion (exploration)
    4, 5, 6 -> Triple(R.drawable.ic_pd_chest, R.drawable.ic_pd_key, R.drawable.ic_pd_potion)
    // Prison: scroll + sword + book (study & combat)
    7, 8, 9 -> Triple(R.drawable.ic_pd_scroll, R.drawable.ic_pd_sword, R.drawable.ic_pd_book)
    // Caves / Mines: chest + sword + amulet (treasure hunting)
    10, 11, 12 -> Triple(R.drawable.ic_pd_chest, R.drawable.ic_pd_sword, R.drawable.ic_pd_amulet)
    // Dwarf City: scroll + wand + potion (magic)
    13, 14, 15 -> Triple(R.drawable.ic_pd_scroll, R.drawable.ic_pd_wand, R.drawable.ic_pd_potion)
    // Evil Eyes / Scorpio: wand + sword + ring (magic combat)
    17, 18 -> Triple(R.drawable.ic_pd_wand, R.drawable.ic_pd_sword, R.drawable.ic_pd_ring)
    // Fists / Last Level: amulet + sword + torch (endgame)
    20, 21, 22, 23 -> Triple(R.drawable.ic_pd_amulet, R.drawable.ic_pd_sword, R.drawable.ic_pd_torch)
    else -> Triple(R.drawable.ic_pd_scroll, R.drawable.ic_pd_sword, R.drawable.ic_pd_potion)
  }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Global brick wall bitmap cache — survives navigation
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
private var cachedBrickBitmap: ImageBitmap? = null
private var cachedBrickKey: Triple<Int, Int, Int>? = null // (hour, w, h)

private fun generateBrickBitmap(w: Int, h: Int, palette: ZonePalette): ImageBitmap {
  val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
  val brickW = 28; val brickH = 14; val gap = 2
  val mortarArgb = palette.mortar.toArgb()
  val wallArgb = palette.wall.toArgb()
  val lightArgb = palette.wallLight.toArgb()
  val darkArgb = palette.wallDark.toArgb()
  val speckArgb = palette.color1.copy(alpha = 0.2f).toArgb()

  // Fill mortar
  bmp.eraseColor(mortarArgb)

  val rows = h / brickH + 2; val cols = w / brickW + 2
  for (row in 0 until rows) {
    val offX = if (row % 2 == 1) brickW / 2 else 0
    for (col in -1 until cols) {
      val bx = col * brickW + offX; val by = row * brickH
      val hash = (row * 137 + col * 269) and 0xFF
      // Body
      for (py in (by + gap) until (by + brickH - gap)) {
        if (py !in 0 until h) continue
        for (px in (bx + gap) until (bx + brickW - gap)) {
          if (px in 0 until w) bmp.setPixel(px, py, wallArgb)
        }
      }
      // Highlight top+left
      val topY = by + gap; val leftX = bx + gap
      if (topY in 0 until h) for (px in (bx + gap) until (bx + brickW - gap)) { if (px in 0 until w) bmp.setPixel(px, topY, lightArgb) }
      if (leftX in 0 until w) for (py in (by + gap) until (by + brickH - gap)) { if (py in 0 until h) bmp.setPixel(leftX, py, lightArgb) }
      // Shadow bottom+right
      val botY = by + brickH - gap - 1; val rightX = bx + brickW - gap - 1
      if (botY in 0 until h) for (px in (bx + gap) until (bx + brickW - gap)) { if (px in 0 until w) bmp.setPixel(px, botY, darkArgb) }
      if (rightX in 0 until w) for (py in (by + gap) until (by + brickH - gap)) { if (py in 0 until h) bmp.setPixel(rightX, py, darkArgb) }
      // Specks — zone-colored dust on bricks
      if (hash % 5 == 0) {
        val sx = (bx + gap + 2 + hash % ((brickW - gap * 2 - 4).coerceAtLeast(1))).coerceIn(0, w - 1)
        val sy = (by + gap + 2 + (hash / 3) % ((brickH - gap * 2 - 4).coerceAtLeast(1))).coerceIn(0, h - 1)
        bmp.setPixel(sx, sy, speckArgb)
      }
      // Extra detail: moss/blood/crystal stains based on zone accent
      if (hash % 11 == 0) {
        val stainArgb = palette.color2.copy(alpha = 0.12f).toArgb()
        val sx2 = (bx + gap + 4 + (hash * 3) % ((brickW - gap * 2 - 6).coerceAtLeast(1))).coerceIn(0, w - 1)
        val sy2 = (by + gap + 2 + (hash / 5) % ((brickH - gap * 2 - 4).coerceAtLeast(1))).coerceIn(0, h - 1)
        bmp.setPixel(sx2, sy2, stainArgb)
        if (sx2 + 1 < w) bmp.setPixel(sx2 + 1, sy2, stainArgb)
      }
    }
  }
  return bmp.asImageBitmap()
}

private fun Color.toArgb(): Int {
  return (((alpha * 255).toInt() shl 24) or ((red * 255).toInt() shl 16) or ((green * 255).toInt() shl 8) or (blue * 255).toInt())
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Composables
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun PixelBrickBackground(modifier: Modifier = Modifier) {
  val config = LocalConfiguration.current
  val density = LocalDensity.current
  val widthPx = with(density) { config.screenWidthDp.dp.toPx().toInt() }.coerceAtLeast(1)
  val heightPx = with(density) { config.screenHeightDp.dp.toPx().toInt() }.coerceAtLeast(1)
  val tick = rememberTimeTick()
  val hour = (tick / 60).coerceIn(0, 23)

  // Global cache — bitmap survives navigation, only regenerates on hour change
  val key = Triple(hour, widthPx, heightPx)
  val brickBitmap = if (cachedBrickKey == key && cachedBrickBitmap != null) {
    cachedBrickBitmap!!
  } else {
    generateBrickBitmap(widthPx, heightPx, currentZonePalette()).also {
      cachedBrickBitmap = it
      cachedBrickKey = key
    }
  }

  Canvas(modifier.fillMaxSize()) {
    drawImage(brickBitmap)
  }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Pixel torch with handle + pixelated flame
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Composable
fun TorchFlame(modifier: Modifier = Modifier.size(32.dp, 56.dp)) {
  val transition = rememberInfiniteTransition(label = "torch")
  val time by transition.animateFloat(
    0f, 6.2832f,
    infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Restart),
    label = "flame"
  )
  val tick = rememberTimeTick()
  val hour = (tick / 60).coerceIn(0, 23)

  // Zone-specific flame color palettes — sourced from MLPD particle/sprite colors
  data class FlameColors(val base: Color, val mid: Color, val bright: Color, val core: Color, val tip: Color, val glow: Color)
  val flames = when (hour) {
    // Demon Halls hours — hellfire red/orange
    0, 16 -> FlameColors(Color(0xFFAA0000), Color(0xFFDD2200), Color(0xFFFF4400), Color(0xFFFF8844), Color(0xFFFFBB88), Color(0xFFFF2200))
    // Burning Fist — intense yellow-orange fire
    1 -> FlameColors(Color(0xFFCC6600), Color(0xFFEE8800), Color(0xFFFFAA00), Color(0xFFFFDD34), Color(0xFFFFEE88), Color(0xFFFFBB00))
    // Rotting Fist — sickly yellow-green
    2 -> FlameColors(Color(0xFF665500), Color(0xFF887722), Color(0xFFAAAA44), Color(0xFFCCCC66), Color(0xFFDDDD88), Color(0xFF999944))
    // Ice Fist — cyan-teal frost
    3 -> FlameColors(Color(0xFF006666), Color(0xFF008888), Color(0xFF26CCC2), Color(0xFF66EEEE), Color(0xFFAAFFFF), Color(0xFF34C9C9))
    // Sewers — toxic green
    4, 5 -> FlameColors(Color(0xFF005500), Color(0xFF008833), Color(0xFF22BB55), Color(0xFF66FF88), Color(0xFFAAFFCC), Color(0xFF33FF66))
    // Garden — warm green-yellow
    6 -> FlameColors(Color(0xFF446600), Color(0xFF668800), Color(0xFF88CC44), Color(0xFFAAEE66), Color(0xFFCCFF88), Color(0xFF88CC44))
    // Prison — lantern warm yellow
    7, 8 -> FlameColors(Color(0xFF886600), Color(0xFFBB8800), Color(0xFFDDAA22), Color(0xFFFFDD44), Color(0xFFFFEE88), Color(0xFFFFDD33))
    // Tengu — combat red-orange
    9 -> FlameColors(Color(0xFF990000), Color(0xFFCC2222), Color(0xFFEE4444), Color(0xFFFF6666), Color(0xFFFF9999), Color(0xFFEE3333))
    // Caves — standard mining torch orange
    10, 11 -> FlameColors(Color(0xFFCC4400), Color(0xFFEE6600), Color(0xFFFF8800), Color(0xFFFFAA00), Color(0xFFFFDD66), Color(0xFFFF9900))
    // Crystal Caves — blue crystal glow
    12 -> FlameColors(Color(0xFF224488), Color(0xFF3366AA), Color(0xFF5588CC), Color(0xFF77AAEE), Color(0xFFAADDFF), Color(0xFF6699DD))
    // Dwarf City — magical blue-white
    13, 14 -> FlameColors(Color(0xFF6666AA), Color(0xFF8888CC), Color(0xFFAAAAEE), Color(0xFFCCCCFF), Color(0xFFEEEEFF), Color(0xFFCCCCFF))
    // Dwarf King — arcane purple
    15 -> FlameColors(Color(0xFF660088), Color(0xFF8822AA), Color(0xFFAA44CC), Color(0xFFCC66EE), Color(0xFFEE99FF), Color(0xFFCC33FF))
    // Evil Eyes — deep purple-pink
    17 -> FlameColors(Color(0xFF550066), Color(0xFF7722AA), Color(0xFF9944CC), Color(0xFFBB66EE), Color(0xFFDD88FF), Color(0xFF9933CC))
    // Scorpio Nest — poison green-purple
    18 -> FlameColors(Color(0xFF004400), Color(0xFF006600), Color(0xFF00BB00), Color(0xFF44FF44), Color(0xFF88FF88), Color(0xFF00FF00))
    // Yog-Dzewa — demonic deep red
    19 -> FlameColors(Color(0xFF880000), Color(0xFFBB0000), Color(0xFFEE0000), Color(0xFFFF4444), Color(0xFFFF8888), Color(0xFFFF0000))
    // Bright Fist — holy white-gold
    20 -> FlameColors(Color(0xFFAA9944), Color(0xFFCCBB66), Color(0xFFEEDD88), Color(0xFFFFEEAA), Color(0xFFFFFFDD), Color(0xFFFFFFAA))
    // Dark Fist — shadow purple
    21 -> FlameColors(Color(0xFF2A1533), Color(0xFF3D2050), Color(0xFF502B66), Color(0xFF6A3D88), Color(0xFF8855AA), Color(0xFF5533AA))
    // Rusted Fist — gray-orange corrosion
    22 -> FlameColors(Color(0xFF885500), Color(0xFFAA7722), Color(0xFFCC9944), Color(0xFFDDBB66), Color(0xFFEEDD88), Color(0xFFBB8833))
    // Last Level — gold-red final
    23 -> FlameColors(Color(0xFFCC6600), Color(0xFFDD8800), Color(0xFFEEAA22), Color(0xFFFFCC44), Color(0xFFFFEE88), Color(0xFFEEAA00))
    else -> FlameColors(Color(0xFFCC0000), Color(0xFFFF6600), Color(0xFFFF8800), Color(0xFFFFFF44), Color(0xFFFFFFCC), Color(0xFFFF6600))
  }

  Canvas(modifier = modifier) {
    val px = size.width / 8f
    val cx = size.width / 2f

    // ── Torch handle ──
    val handleTop = size.height * 0.65f
    drawRect(Color(0xFF8B4513), Offset(cx - px, handleTop), Size(px * 2, size.height - handleTop))
    drawRect(Color(0xFF5D3A0E), Offset(cx - px, size.height - px), Size(px * 2, px))
    drawRect(Color(0xFF3D2E18), Offset(cx - px * 1.5f, handleTop), Size(px * 3, px))

    // ── Ember glow ──
    val flameBase = handleTop - px
    drawCircle(
      brush = Brush.radialGradient(
        listOf(flames.glow.copy(alpha = 0.35f), flames.glow.copy(alpha = 0.12f), Color.Transparent),
        center = Offset(cx, flameBase), radius = size.width * 1.0f
      ), center = Offset(cx, flameBase), radius = size.width * 1.0f
    )

    // ── Pixelated flame particles with zone colors ──
    data class FlamePixel(val relX: Float, val relY: Float, val color: Color, val phase: Float)

    val f = flames
    val particles = listOf(
      // Base layer — wide
      FlamePixel(-2f, 0f, f.base, 0f), FlamePixel(-1f, 0f, f.mid, 0.5f),
      FlamePixel(0f, 0f, f.mid, 1f), FlamePixel(1f, 0f, f.mid, 1.5f), FlamePixel(2f, 0f, f.base, 2f),
      // Mid layer
      FlamePixel(-1.5f, -1f, f.mid, 0.3f), FlamePixel(-0.5f, -1f, f.bright, 0.8f),
      FlamePixel(0.5f, -1f, f.bright, 1.3f), FlamePixel(1.5f, -1f, f.mid, 1.8f),
      // Upper
      FlamePixel(-1f, -2f, f.bright, 0.4f), FlamePixel(0f, -2f, f.core, 1.0f), FlamePixel(1f, -2f, f.bright, 1.6f),
      // Core
      FlamePixel(-0.5f, -3f, f.core, 0.6f), FlamePixel(0.5f, -3f, f.core, 1.2f),
      // Tip
      FlamePixel(0f, -4f, f.tip, 0.9f), FlamePixel(0f, -5f, f.tip, 1.5f),
    )

    for (p in particles) {
      val flicker = sin(time * 4f + p.phase * 2f)
      val sway = sin(time * 2.5f + p.phase) * px * 0.4f
      val jumpY = flicker * px * 0.3f
      val alpha = (0.75f + flicker * 0.25f).coerceIn(0.4f, 1f)

      drawRect(
        color = p.color.copy(alpha = alpha),
        topLeft = Offset(cx + p.relX * px + sway, flameBase + p.relY * px + jumpY),
        size = Size(px, px)
      )
    }
  }
}

/** Torch glow + vignette + actual flame sprites at corners */
@Composable
fun TorchGlowOverlay(modifier: Modifier = Modifier) {
  val transition = rememberInfiniteTransition(label = "glow")
  val flicker by transition.animateFloat(
    0.4f, 0.7f,
    infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
    label = "flicker"
  )
  val tick = rememberTimeTick()
  val hour = (tick / 60).coerceIn(0, 23)
  val palette = currentZonePalette()

  // Torch glow color — zone's signature glow, sourced from MLPD lighting
  val torchColor = when (hour) {
    0, 16    -> Color(0xFFFF2200)  // Halls — hellfire red
    1        -> Color(0xFFFFBB00)  // Burning — intense yellow
    2        -> Color(0xFF999944)  // Rotting — sickly yellow-green
    3        -> Color(0xFF34C9C9)  // Ice — cyan-teal
    4, 5     -> Color(0xFF33FF66)  // Sewers — toxic green
    6        -> Color(0xFF88CC44)  // Garden — leaf green
    7, 8     -> Color(0xFFFFDD33)  // Prison — lantern yellow
    9        -> Color(0xFFEE3333)  // Tengu — combat red
    10, 11   -> Color(0xFFFF9900)  // Caves — mining torch orange
    12       -> Color(0xFF6699DD)  // Crystal — blue glow
    13, 14   -> Color(0xFFEEEEFF)  // City — magical white-blue
    15       -> Color(0xFFCC33FF)  // Dwarf King — arcane purple
    17       -> Color(0xFF9933CC)  // Evil Eyes — deep purple
    18       -> Color(0xFF00FF00)  // Scorpio — poison green
    19       -> Color(0xFFFF0000)  // Yog — demonic red
    20       -> Color(0xFFFFFFAA)  // Bright — holy white
    21       -> Color(0xFF5533AA)  // Dark — shadow purple
    22       -> Color(0xFFBB8833)  // Rusted — corrosion orange
    23       -> Color(0xFFEEAA00)  // Last — gold
    else     -> Color(0xFFFF8800)
  }

  // Secondary glow — magic pool at bottom, color varies by chapter
  val poolColor = when (hour) {
    in 0..3, in 19..23 -> Color(0xFFFF4400).copy(alpha = flicker * 0.06f)   // Halls — lava pool
    in 4..6            -> Color(0xFF00FF88).copy(alpha = flicker * 0.08f)    // Sewers — green sewer water
    in 7..9            -> Color(0xFFFFDD33).copy(alpha = flicker * 0.06f)    // Prison — candlelight
    in 10..12          -> Color(0xFF66B3FF).copy(alpha = flicker * 0.10f)    // Caves — crystal reflection
    in 13..15          -> Color(0xFF00FFFF).copy(alpha = flicker * 0.08f)    // City — MLPD cyan magic
    in 16..18          -> Color(0xFFFF0044).copy(alpha = flicker * 0.07f)    // Halls — blood pool
    else               -> Color(0xFF00FFFF).copy(alpha = flicker * 0.08f)
  }

  Box(modifier = modifier.fillMaxSize()) {
    // Glow spots on brick wall
    Canvas(Modifier.fillMaxSize()) {
      // Top-left torch glow
      drawCircle(
        brush = Brush.radialGradient(
          listOf(torchColor.copy(alpha = flicker * 0.25f), Color.Transparent),
          center = Offset(size.width * 0.08f, size.height * 0.06f), radius = size.width * 0.5f
        ), center = Offset(size.width * 0.08f, size.height * 0.06f), radius = size.width * 0.5f
      )
      // Top-right torch glow
      drawCircle(
        brush = Brush.radialGradient(
          listOf(torchColor.copy(alpha = flicker * 0.2f), Color.Transparent),
          center = Offset(size.width * 0.92f, size.height * 0.06f), radius = size.width * 0.45f
        ), center = Offset(size.width * 0.92f, size.height * 0.06f), radius = size.width * 0.45f
      )
      // Bottom center — zone-specific magic pool glow
      drawCircle(
        brush = Brush.radialGradient(
          listOf(poolColor, Color.Transparent),
          center = Offset(size.width * 0.5f, size.height * 0.92f), radius = size.width * 0.5f
        ), center = Offset(size.width * 0.5f, size.height * 0.92f), radius = size.width * 0.5f
      )

      // Vignette — dark dungeon edges
      drawRect(
        Brush.radialGradient(
          listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
          Offset(size.width / 2f, size.height / 2f), size.width * 0.8f
        )
      )
    }

    // Actual animated pixel torch flames at top corners
    TorchFlame(
      Modifier.align(Alignment.TopStart).offset(x = 6.dp, y = 6.dp).size(32.dp, 56.dp)
    )
    TorchFlame(
      Modifier.align(Alignment.TopEnd).offset(x = (-6).dp, y = 6.dp).size(32.dp, 56.dp)
    )
  }
}
