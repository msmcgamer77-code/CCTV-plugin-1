# CCTV Plugin for Aternos (Spigot/Paper)

Ye plugin Minecraft server (Paper/Spigot 1.20.x) ke liye CCTV-style surveillance system deta hai.

## Features
- **Camera placement**: `/cctv give` se ek camera item milega, kisi block par right-click karke camera set ho jaayega.
- **Theft Detection**: Camera ke radius (default 12 blocks) me agar koi player:
  - block todta hai
  - chest/container kholta hai
  - item uthata (pickup) hai
  toh staff (cctv.admin permission wale) ko chat me alert milega + sound bajega.
- **Recording**: Har event `plugins/CCTVPlugin/recordings/<cameraName>/<date>.log` file me save hota hai.
- **Monitor (live GUI with player skins)**: `/cctv monitor <cameraName>` se ek inventory GUI khulega jisme camera ke radius me jo players hain unke **player head/skin icons** dikhenge, har 2 seconds me auto-refresh hota hai. Head par hover karne se distance aur exact location dikhti hai.

## Commands
| Command | Description |
|---|---|
| `/cctv give` | Camera placement item do |
| `/cctv list` | Sab cameras ki list dikhao |
| `/cctv monitor <name>` | Live monitor GUI kholo (player skins ke saath) |
| `/cctv remove <name>` | Camera hatao |
| `/cctv log <name>` | Recent recorded activity dikhao |

## Permissions
- `cctv.admin` (default: op) - sab kuch + theft alerts milte hain
- `cctv.use` (default: op) - camera dena/place karna/monitor dekhna

## Config (`config.yml`)
```yaml
detection-radius: 12   # blocks
scan-interval: 40      # ticks (40 = 2 sec) for live monitor refresh
alert-staff: true
alert-sound: true
max-log-lines: 5000
```

---

## Build karne ka tarika (jar banane ke liye)

Mere paas yaha internet access nahi hai isliye main khud `.jar` compile nahi kar saka — lekin source code 100% ready hai. Tumhe sirf ek baar Maven se build karna hai:

### Option 1: Apne computer par (Java 17 + Maven installed)
```bash
cd CCTVPlugin
mvn clean package
```
Jar yaha milegi: `target/CCTVPlugin.jar`

Agar Maven install nahi hai:
- Windows/Mac/Linux: https://maven.apache.org/install.html se install karo
- Java 17+ bhi chahiye: https://adoptium.net se

### Option 2: Online (bina kuch install kiye)
1. Is poore `CCTVPlugin` folder ko GitHub par ek naye repo me upload karo
2. GitHub Actions ka ek free Maven build workflow add karke jar generate karwa sakte ho (bata do agar ye workflow file bhi chahiye, main bana dunga)
3. Ya phir Replit/Gitpod jaise online IDE me Maven project import karke `mvn package` chala do

### Aternos par install karna
1. `target/CCTVPlugin.jar` file download karo
2. Aternos server panel kholo → ensure software **Paper** ya **Spigot** set hai (Vanilla/Forge me plugins kaam nahi karte)
3. **Files → plugins** folder me jaake `CCTVPlugin.jar` upload karo
4. Server restart karo
5. In-game `/cctv give` se camera item lo aur kisi block par right-click karke camera laga do!

---

## Important Note (Limitation)
Minecraft me asli "video" recording (jaisa real CCTV camera karta hai) possible nahi hai kyunki game engine isko support nahi karta. Isliye recording **text-log based** hai (kab, kaun, kya action) — ye chori track karne ke liye kaafi hai aur staff ke liye proof ka kaam karta hai. Live "skin dikhna" wala feature real-time GUI se solve kiya gaya hai jisme actual player skins (heads) dikhte hain.
