package com.example.pdtranslator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pdtranslator.ui.theme.PDTranslatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDTranslatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TranslatorScreen()
                }
            }
        }
    }
}

@Composable
fun TranslatorScreen(modifier: Modifier = Modifier) {
    var text1 by remember { mutableStateOf("超越光与暗的领域，直达于难以抑止的太虚；就在这座隐藏在宇宙之中的、令人作呕的墓地里，从超越时间、超越想象的黑暗房间中传来疯狂敲打巨鼓的声响，以及长笛细微、单调、亵渎的音色。应和这可憎的敲击和吹奏，那些庞大而黑暗的终极之神——那些盲目、喑哑、痴愚的蕃神们——正缓慢、笨拙、荒谬地跳起舞蹈。而它们的灵魂就是奈亚拉托提普。\n\n这是它在这个宇宙的化身之一，但灵魂早已不在，祂利用它来传播混沌，传播痴愚……\n\n_攻击时有 50% 概率在场上创建一个不隐藏的随机陷阱（不含造成上下楼效果的陷阱）\n玩家直接踩到陷阱时受到 10 点心魔损伤（翻倍后为 20 点）\n奈亚子的近战攻击会造成 7~14 点心魔损伤。\n奈亚子丢失目标时，每 3 回合会在场上随机位置瞬移。\n\n真实心魔（被动）：奈亚子在场期间，玩家受到的心魔损伤翻倍；且玩家每次受到心魔损伤时，会同时受到相当于该损伤值 50%~100% 的魔法伤害。通过 “真实心魔” 效果造成的魔法伤害，将为场上所有单位回复对应数值的生命值。_") }
    var text2 by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = text1,
            onValueChange = { text1 = it },
            label = { Text("文本1") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        OutlinedTextField(
            value = text2,
            onValueChange = { text2 = it },
            label = { Text("文本2") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslatorScreenPreview() {
    PDTranslatorTheme {
        TranslatorScreen()
    }
}
