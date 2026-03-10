# Aim Assist Mod - Fabric 1.21.1

Dushmanlarga 7-10 blokdan avtomatik nishon oluvchi mod.

## Xususiyatlari
- 7 blok ichida to'liq kuchli aim assist
- 7-10 blok oralig'ida silliq kuchsizlanish
- Faqat hostile mob'larni nishon oladi (skeletonlar, zombilar, creeper va boshqalar)
- Silliq crosshair harakati

## Tugmalar
| Tugma | Amal |
|-------|------|
| **R** | Aim Assist yoqish/o'chirish |
| **+** | Diapazoni oshirish |
| **-** | Diapazoni kamaytirish |

## O'rnatish

### Talab qilinadigan dasturlar
- Java 21
- Gradle 8.x

### Qurish (Build)
```bash
./gradlew build
```
Jar fayl `build/libs/aimassist-1.0.0.jar` da paydo bo'ladi.

### O'rnatish
1. [Fabric Loader](https://fabricmc.net/use/) o'rnating (1.21.1 versiyasi)
2. [Fabric API](https://modrinth.com/mod/fabric-api) yuklab oling
3. `aimassist-1.0.0.jar` faylini `.minecraft/mods/` papkasiga qo'ying
4. Minecraft'ni ishga tushiring

## Kod tuzilishi
```
src/main/java/com/aimassist/
├── AimAssistMod.java       - Asosiy mod va aim logikasi
├── AimAssistKeybinds.java  - Klaviatura tugmalari
└── mixin/
    └── GameRendererMixin.java - Render mixin
```

## Sozlash (kodda)
`AimAssistMod.java` da quyidagi o'zgaruvchilarni o'zgartiring:
- `aimRange = 7.0` - Asosiy diapazon
- `maxRange = 10.0` - Maksimal diapazon
- `aimStrength = 0.15f` - Aim kuchi (0.0-1.0)
- `smoothness = 0.08f` - Silliqlik
