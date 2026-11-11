# Block Blast Lite — აშენების გეგმა

**იდეა №1** (კვლევა: `research_notes/popular-apps-games-ideas-nov2026-20261111-1059/report.md`)
**სტატუსი:** მხოლოდ გეგმა — აშენება იწყება მხოლოდ შაოს „დაიწყე"-ს შემდეგ.
**თარიღი:** 2026-11-12

---

## 1. რა არის თამაში

8×8 დაფაზე ბლოკების თავსატეხი. მოთამაშეს ქვემოთ 3 ფიგურა ეძლევა, თითოეულს ათრევს დაფაზე და ათავსებს. როცა მთლიანი მწკრივი ან სვეტი ივსება — იწმინდება და ქულა ერიცხება. თამაში მთავრდება, როცა სამი ფიგურიდან არცერთი აღარ ეტევა დაფაზე.

- არ არის ტაიმერი — მშვიდი, უსასრულო თამაში.
- სრულად ოფლაინ — ინტერნეტი არ სჭირდება, ანგარიში არ სჭირდება.
- ორიგინალური გრაფიკა — სხვისი ასეტების კოპირება არა.

**რატომ ეს იდეა:** Block Blast! ახლა მსოფლიოში #1–#3 ჩამოტვირთვადი თამაშია (1.1 მილიარდი ინსტალაცია, Sensor Tower, ოქტომბერი 2026). მექანიკა მარტივია — დამწყები დეველოპერის ზომისაა.

---

## 2. თამაშის წესები (ზუსტად)

1. დაფა: 8×8 უჯრა.
2. უჯრაში (tray) ერთდროულად 3 ფიგურაა. ფიგურები: 1×1, 1×2, 1×3, 1×4, 2×2, 2×3, 3×3, L-ფორმა, T-ფორმა, Z-ფორმა (სულ ~15 ფორმა).
3. ფიგურის დადება შეიძლება მხოლოდ ცარიელ უჯრებზე; გადაფარვა არ შეიძლება.
4. როცა ფიგურა დაიდო, უჯრაში მის ადგილას ახალი არ ჩნდება — სამივე რომ დაიდო, 3 ახალი ფიგურა მოდის.
5. სრული მწკრივი ან სვეტი იწმინდება მაშინვე. ერთდროულად რამდენიმე ხაზი = კომბო.
6. თამაში მთავრდება, როცა სამივე ფიგურიდან არცერთი აღარ ეტევა დაფაზე.

**ქულები:**
- თითო დადებული უჯრა = 1 ქულა.
- თითო გასუფთავებული ხაზი = 10 ქულა.
- კომბო: 2 ხაზი ერთად = ×2, 3 ხაზი = ×3 (ხაზების ქულაზე).
- ზედიზედ გასუფთავება (streak): ყოველი მომდევნო სვლა, რომელშიც ხაზი იწმინდება = +5 ქულა ბონუსი.

---

## 3. რეჟიმები

### 3.1 უსასრულო (Endless) — მთავარი რეჟიმი
ითამაშე სანამ ფიგურა ეტევა. რეკორდი ინახება ტელეფონში (Room).

### 3.2 დონეები (100 დონე)
- თითოეულ დონეს აქვს **მიზნობრივი ქულა** და **სვლების ლიმიტი** (მაგ. დონე 1: 100 ქულა 20 სვლაში).
- მიზანს მიაღწიე → დონე გავლილია, შემდეგი იხსნება.
- პირველი 10 დონე მარტივი (მიზანი დაბალი, სვლა ბევრი); შემდეგ რთულდება.
- დონეები წინასწარ განსაზღვრულია (JSON ფაილი აპში), შემთხვევითი არ არის.

### 3.3 ყოველდღიური (Daily)
- ერთი და იგივე ფიგურების მიმდევრობა მთელი დღის განმავლობაში (seed = თარიღი).
- შედეგი იზიარება ემოჯი-ბადით (Wordle-ის სტილში), ანგარიშის გარეშე.

### 3.4 ანბანის რეჟიმი (ქართული twist)
- ჩვეულებრივი თამაში, მაგრამ თითოეულ ფიგურას ქართული ასო აქვს მიბმული (ა, ბ, გ…).
- როცა ხაზი იწმინდება, იმ ხაზის ასოები გამოჩნდება სახელით („ა — ანი").
- მიზანი: თამაშით ქართული ანბანის სწავლა. არცერთ დიდ თავსატეხ აპს ეს არ აქვს.

---

## 4. ეკრანები

1. **მთავარი მენიუ:** თამაში (უსასრულო), დონეები, ყოველდღიური, ანბანი, რეკორდები, პარამეტრები.
2. **თამაშის ეკრანი:** დაფა, ქულა, რეკორდი, უჯრა 3 ფიგურით, პაუზა.
3. **თამაში დასრულდა:** ქულა, რეკორდი (ახალია თუ არა), თავიდან / მენიუში.
4. **დონეების სია:** 100 დონე, ჩაკეტილი/გავლილი ნიშნით.
5. **პარამეტრები:** ხმა ჩართ/გამორთ, ვიბრაცია, ენა (KA/EN/RU), თემა.

---

## 5. დიზაინი

- **ფერები (შაოს ფერები):** შავი `#0A0A0A`, მუქი ლურჯი `#12275C`, თეთრი `#FFFFFF`.
- **ენა:** ქართული (ნაგულისხმევი), ინგლისური, რუსული — ყველაფერი ნათარგმნი.
- **სტილი:** ბრტყელი, მინიმალისტური; ფიგურები ფერადი (ლურჯი/მწვანე/ყვითელი/წითელი/იისფერი), დაფა მუქი.
- **ხმა:** მარტივი ჩაშენებული ტონები (დადება, გასუფთავება, კომბო, დამარცხება) — ცალკე აუდიო ფაილები არა.
- **შრიფტი:** სისტემური (ქართული სწორად უნდა გამოჩნდეს).

---

## 6. ტექნიკური არქიტექტურა

- **სტეკი:** Kotlin + Jetpack Compose + Room (იგივე, რაც IR Hack Remote-ში).
- **minSdk 26, targetSdk 34.** ინტერნეტის ნებართვა **არ არის** — სრულად ოფლაინ.
- **მონაცემები (Room):**
  - `scores` — რეკორდი (უსასრულო), თარიღი.
  - `levels` — დონის №, გავლილია/არა, საუკეთესო ქულა.
  - `daily` — თარიღი, ქულა, შესრულებულია/არა.
  - `settings` — ხმა, ვიბრაცია, ენა.
- **თამაშის ლოგიკა:** ცალკე კლასი (`GameEngine`) — დაფის მდგომარეობა, დადების შემოწმება, ხაზების გასუფთავება, ქულები, დამარცხების შემოწმება. UI-სგან დამოუკიდებელი, რომ ტესტირება შეიძლებოდეს.
- **Drag & drop:** Compose pointer input — ფიგურა თითით ითრევა, დაფაზე ghost-პრევიუ ჩანს, ვარდნა უჯრაზე.
- **შემთხვევითობა:** ფიგურების გენერატორი seeded Random-ით (ყოველდღიური რეჟიმისთვის seed = თარიღი).

---

## 7. აშენების ნაბიჯები (თანმიმდევრობით)

1. **პროექტის ჩონჩხი** — Compose + Room, მთავარი მენიუ, ნავიგაცია, ფერები, ენები.
2. **დაფა + drag & drop** — ფიგურის ათრევა, დადების შემოწმება, ghost-პრევიუ.
3. **ხაზების გასუფთავება + ქულები + დამარცხება** — GameEngine სრულად.
4. **უსასრულო რეჟიმი + რეკორდი** — Room-ში შენახვა, რეკორდების ეკრანი.
5. **დონეები** — 100 დონის JSON, მიზანი/სვლები, გახსნის ლოგიკა.
6. **ყოველდღიური რეჟიმი** — seed თარიღით, გაზიარების ემოჯი-ბადე.
7. **ანბანის რეჟიმი** — ასოების მიბმა ფიგურებზე, ასოების ჩვენება.
8. **პარამეტრები + ხმები + გაპრიალება** — ხმის ტონები, ვიბრაცია, ანიმაციები.
9. **რელიზი** — APK + AAB + source ZIP (Play Store-ისთვის).
10. **AdMob (მოგვიანებით, ცალკე ნაბიჯი)** — ბანერი/ინტერსტიციული; საქართველოდან ფასიანი აპი და IAP შეუძლებელია, მხოლოდ რეკლამა.

---

## 8. რას არ ვაკეთებთ (scope control)

- არ არის ონლაინ ლიდერბორდი, არ არის ანგარიში, არ არის სერვერი.
- არ არის ფასიანი ფუნქციები / IAP (საქართველოდან შეუძლებელია).
- არ არის სხვისი გრაფიკა/ხმა — ყველაფერი ორიგინალური ან ჩაშენებული.
- AdMob მხოლოდ რელიზის შემდეგ, ცალკე ნაბიჯად — MVP-ში არა.

---

## 9. მიღების კრიტერიუმები

- `assembleDebug` უშეცდომოდ იშენება.
- ფიგურის დადება მხოლოდ ცარიელ უჯრებზე შეიძლება; გადაფარვა შეუძლებელია.
- სრული ხაზი იწმინდება მაშინვე; ქულები წესების მიხედვით ერიცხება.
- დამარცხება ზუსტად მაშინ ფიქსირდება, როცა არცერთი ფიგურა აღარ ეტევა.
- რეკორდი და დონეების პროგრესი აპის გადატვირთვის შემდეგაც შენახულია.
- ინტერნეტის ნებართვა არ არის; აპი ოფლაინ სრულად მუშაობს.
- ქართული UI სრულია, ქართული ტექსტი სწორად ჩანს.

---

## 10. მზა პრომტი Claude-ისთვის (ინგლისურად, კოპირება-ჩასმა)

```text
Build a complete Android app "Block Blast Lite" — an 8x8 block puzzle game.

STACK: Kotlin, Jetpack Compose (Material 3), Room. minSdk 26, targetSdk 34.
NO internet permission. Fully offline, no accounts, no server.

GAME RULES:
- 8x8 board. A tray shows 3 block shapes at a time (shapes: 1x1, 1x2, 1x3,
  1x4, 2x2, 2x3, 3x3, L, T, Z — about 15 shapes total).
- Drag a shape onto the board; it can only be placed on empty cells, no overlap.
- When all 3 tray shapes are placed, 3 new shapes appear.
- A full row or column clears immediately. Multiple lines at once = combo.
- Game over when none of the 3 tray shapes fits anywhere on the board.
- Scoring: 1 point per placed cell; 10 points per cleared line; combo x2 for
  2 lines at once, x3 for 3 lines; +5 streak bonus for clearing a line on
  consecutive moves.

MODES:
1. Endless — play until no shape fits; best score saved locally.
2. Levels — 100 predefined levels in a bundled JSON file; each level has a
   target score and a move limit; completing a level unlocks the next.
3. Daily — same shape sequence all day (seed = date); shareable emoji-grid
   result, no account.
4. Alphabet mode — normal game, but each shape carries a Georgian letter
   (ა, ბ, გ...); when a line clears, show the letters with their names
   (e.g. "ა — ანი").

SCREENS: Main menu (Endless, Levels, Daily, Alphabet, Records, Settings),
game screen (board, score, best, 3-shape tray, pause), game-over screen
(score, new-best badge, retry/menu), levels list (100 levels,
locked/completed marks), settings (sound, vibration, language KA/EN/RU).

DESIGN: Colors strictly Black #0A0A0A, Dark Blue #12275C, White #FFFFFF.
Georgian UI by default, fully translated to English and Russian. Flat,
minimalist style; colorful pieces (blue/green/yellow/red/purple) on a dark
board. Sounds are simple built-in tones only (place, clear, combo, game
over) — no audio files. System font (must render Georgian correctly).

ARCHITECTURE: Separate GameEngine class (board state, placement validation,
line clearing, scoring, game-over check) independent of UI. Room tables:
scores (best endless score + date), levels (number, completed, best score),
daily (date, score), settings (sound, vibration, language). Drag & drop with
Compose pointer input, ghost preview on the board while dragging. Seeded
Random for shape generation (daily mode seed = date).

DO NOT BUILD: online leaderboards, accounts, servers, paid features, IAP,
third-party graphics or sounds. AdMob is a LATER step, not in this build.

DELIVERABLES: working Android Studio project, assembleDebug must build,
APK + AAB + source ZIP.

ACCEPTANCE CRITERIA:
- assembleDebug builds with no errors.
- Placement only on empty cells; overlap impossible.
- Full lines clear immediately; scoring follows the rules above.
- Game over triggers exactly when no tray shape fits.
- Best score and level progress persist after app restart.
- No INTERNET permission; app works fully offline.
- Georgian UI complete and renders correctly.
```

---

## 11. დამატებითი იდეები (მომავალი, გადაუწყვეტელი)

ეს იდეები MVP-ში **არ შედის**. გეგმაში ჩაწერილია, რომ არ დაიკარგოს; რომელი შევა, შაო წყვეტს.
ჩემი რეკომენდაცია: კომბოს ანიმაციები + მონეტები აუცილებელია; სტატისტიკა + მეორე შანსი — მეორე ტალღის ყველაზე ძლიერი.

**პირველი ხუთეული:**
1. **კომბოს ანიმაციები** — ხაზის გასუფთავებისას აფეთქება/მოციმციმე, მცურავი ქულა, დიდ კომბოზე ეკრანის მსუბუქი რყევა.
2. **ოფლაინ მონეტები + მაღაზია** — გასუფთავებაზე მონეტა გროვდება, იხარჯება თემებსა და ბუსტერებზე. გადახდა არსად — მხოლოდ თამაშით.
3. **ბუსტერები** — ჩაქუჩი (ერთი უჯრის წაშლა), არევა (ახალი ფიგურები), ბომბი (3×3 გასუფთავება). თამაშით იშოვება.
4. **მისიები / მიღწევები** — „ერთ თამაშში 5 ხაზი", „1000 ქულა" და ა.შ., ბეჯებით.
5. **ქულის ბარათის გაზიარება** — ლამაზი PNG ქულით (სიჩქარის ტესტის ბარათის მსგავსად), ინსტაგრამზე გასაზიარებლად.

**მეორე ტალღა:**
6. **ყოველდღიური ჯილდო (streak)** — ყოველ დღე შესვლაზე მონეტა + ბონუსი; ზედიზედ დღეები ითვლება, გაცდენაზე წყდება.
7. **სტატისტიკა** — სულ თამაშები, გასუფთავებული ხაზები, საუკეთესო კომბო, საშუალო ქულა.
8. **მეორე შანსი** — დამარცხებისას მონეტით გაგრძელება: დაფა ისე რჩება, 3 ახალი ფიგურა მოდის.
9. **სირთულის არჩევანი** — მარტივი / ნორმალური / რთული (მარტივში პატარა ფიგურები, რთულში დიდი და უხერხული).
10. **თემები** — ფერადი თემები მონეტით იხსნება (ნეონი, პასტელი, საქართველოს ფერები); ნაგულისხმევი შავი/ლურჯია.
11. **ფერადბრმა რეჟიმი** — ფიგურებზე ნიშნები/ფორმები, არა მხოლოდ ფერი.
