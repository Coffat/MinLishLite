---
name: MinLish Lite Minimal
colors:
  primary: "#2563EB"
  primary-soft: "#EFF6FF"
  secondary: "#64748B"
  accent-green: "#16A34A"
  accent-orange: "#F97316"
  accent-yellow: "#EAB308"
  background: "#F8FAFC"
  surface: "#FFFFFF"
  surface-variant: "#F1F5F9"
  border: "#E2E8F0"
  on-surface: "#0F172A"
  on-surface-muted: "#64748B"
  error: "#EF4444"
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: 700
    lineHeight: 44px
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: 700
    lineHeight: 36px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: 700
    lineHeight: 32px
  title-lg:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: 600
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: 600
    lineHeight: 26px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: 400
    lineHeight: 22px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
rounded:
  sm: 8px
  md: 12px
  lg: 16px
  xl: 20px
  full: 999px
spacing:
  xs: 4px
  sm: 8px
  md: 12px
  lg: 16px
  xl: 24px
  2xl: 32px
shadow:
  sm: "0 1px 2px rgba(15, 23, 42, 0.06)"
  md: "0 4px 12px rgba(15, 23, 42, 0.08)"
---

# Design System

## Overview

**MinLish Lite Minimal** is a clean, modern, and student-friendly interface for an Android vocabulary learning app.

The design direction is:

- Minimal and realistic
- Light theme first
- Low visual noise
- Clear learning hierarchy
- Fast to scan
- Easy to implement with Jetpack Compose
- Not overly colorful or glossy
- Suitable for a basic Android project but still polished enough for presentation

The app should feel like a real productivity/learning tool, not a decorative AI-generated concept. The interface relies on whitespace, simple cards, clear typography, subtle dividers, and a restrained blue accent.

---

## Design Principles

### 1. Minimal but not empty

Use whitespace generously, but every screen must still feel useful and informative.

Good examples:

- Home screen shows learning summary, today’s review, and deck preview.
- Deck screen shows search, filters, deck list, and progress.
- Study screen focuses only on the current flashcard and review actions.

### 2. One primary action per screen

Each screen should have only one dominant action.

Examples:

- Home: **Study Now**
- Deck List: **New Deck**
- Add Word: **Save Word**
- Study: **Flip Card** or review buttons
- Settings: no dominant CTA unless saving is required

### 3. Blue is the brand action color

Primary blue is used for:

- Main CTA
- Active navigation item
- Active filter chip
- Links
- Progress bars
- Focused input state

Do not overuse blue for every icon or every piece of text.

### 4. Semantic colors are limited

Use green, orange, yellow, and red only when they carry meaning:

- Green: learned, success, easy, good retention
- Orange: due today, hard, warning-like study reminder
- Yellow: medium/good review state
- Red: error, destructive action, again/failed review

### 5. UI should support learning speed

The user should quickly understand:

- What to learn today
- Which words are due
- How far they have progressed
- What action to take next

---

## Colors

### Core Palette

- **Primary** (#2563EB): Main CTAs, active states, selected tabs, progress bars, important links
- **Primary Soft** (#EFF6FF): Soft background for selected chips, icon containers, light emphasis states
- **Secondary** (#64748B): Secondary labels, subtitles, inactive icons, helper text
- **Background** (#F8FAFC): App background
- **Surface** (#FFFFFF): Cards, forms, bottom navigation, input containers
- **Surface Variant** (#F1F5F9): Soft icon backgrounds, disabled surfaces, subtle grouped areas
- **Border** (#E2E8F0): Card border, input border, dividers
- **On Surface** (#0F172A): Primary text
- **On Surface Muted** (#64748B): Secondary text
- **Error** (#EF4444): Validation errors, destructive actions, failed review states

### Semantic Colors

- **Accent Green** (#16A34A): Learned status, Easy, success, retention, positive stat
- **Accent Orange** (#F97316): Due Today, Hard, urgent review
- **Accent Yellow** (#EAB308): Medium, Good, moderate status
- **Error** (#EF4444): Again, invalid input, delete confirmation

### Color Usage Rules

Do:

- Use blue for the most important action.
- Use muted gray for secondary information.
- Use green/orange/yellow only for status meaning.
- Keep most backgrounds white or very light gray.

Don’t:

- Don’t use blue on every icon.
- Don’t use gradients as the main visual style.
- Don’t mix too many pastel blocks on one screen.
- Don’t use saturated colors for large backgrounds.
- Don’t use red except for error/destructive/Again state.

---

## Typography

### Font

Use **Inter** as the primary font.

If Inter is not available in Android project setup, use:

- `FontFamily.Default` during development
- Later replace with Inter through font resources

### Type Scale

#### Display Large

Used for onboarding title or major brand title.

- Font: Inter
- Size: 36px
- Weight: 700
- Line height: 44px

#### Headline Large

Used for page titles such as:

- Your Decks
- Settings
- Progress

- Font: Inter
- Size: 28px
- Weight: 700
- Line height: 36px

#### Headline Medium

Used for section-heavy screen titles.

- Font: Inter
- Size: 24px
- Weight: 700
- Line height: 32px

#### Title Large

Used for card titles and important item names.

- Font: Inter
- Size: 20px
- Weight: 600
- Line height: 28px

#### Title Medium

Used for list item titles and form section titles.

- Font: Inter
- Size: 18px
- Weight: 600
- Line height: 26px

#### Body Large

Used for main readable content.

- Font: Inter
- Size: 16px
- Weight: 400
- Line height: 24px

#### Body Medium

Used for helper text, subtitles, descriptions.

- Font: Inter
- Size: 14px
- Weight: 400
- Line height: 22px

#### Label Medium

Used for chips, status labels, metadata.

- Font: Inter
- Size: 12px
- Weight: 500
- Line height: 16px

### Typography Rules

Do:

- Use bold only for hierarchy, not decoration.
- Keep body text between 14–16px.
- Use muted text for helper descriptions.
- Use sentence case for labels and buttons.

Don’t:

- Don’t use uppercase section headers everywhere.
- Don’t use more than 3 text sizes in one card.
- Don’t make stat numbers too decorative.
- Don’t use italic except for part of speech, such as `adj.`, `v.`, `n.`.

---

## Spacing

Use an 8px-based spacing system.

- **4px**: Tiny gap between icon and label
- **8px**: Small internal spacing
- **12px**: Field spacing, chip padding
- **16px**: Standard screen padding and card padding
- **24px**: Section spacing
- **32px**: Large vertical separation

### Screen Padding

Recommended default:

```kotlin
Modifier.padding(horizontal = 20.dp)
```

For dense screens:

```kotlin
Modifier.padding(horizontal = 16.dp)
```

### Component Spacing Rules

- Space between section title and content: 12–16px
- Space between cards: 12–16px
- Space inside cards: 16px
- Space between input fields: 16px
- Space between bottom navigation items: equal distribution

---

## Rounded Corners

- **Small**: 8px — chips, tiny badges
- **Medium**: 12px — inputs, small buttons
- **Large**: 16px — cards, list containers
- **Extra Large**: 20px — large panels, study cards
- **Full**: 999px — pills, circular buttons, toggles

### Rounded Rules

Do:

- Use 12–16px for most components.
- Use 20px for large flashcards.
- Use full radius for chips and toggles.

Don’t:

- Don’t mix sharp corners with rounded cards.
- Don’t use extremely round cards everywhere.
- Don’t use inconsistent corner radius on the same screen.

---

## Elevation & Borders

The app should feel light and minimal. Prefer borders over heavy shadows.

### Cards

Default card style:

- Background: Surface
- Border: 1px solid Border
- Radius: 16px
- Shadow: subtle only

Recommended:

```kotlin
Card(
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
)
```

### Shadows

Use shadows sparingly:

- Small cards: 0–1dp
- Important cards: 2dp
- Floating action button: 4dp

Don’t use large blurred shadows.

---

## Components

## Buttons

### Primary Button

Used for the main action on a screen.

Examples:

- Study Now
- Save Word
- Get Started
- New Deck

Style:

- Background: Primary
- Text: White
- Height: 52–56dp
- Radius: 12–14dp
- Font: 16px, 600
- Optional leading icon

Use only one primary button per screen.

### Secondary Button

Used for less important actions.

Examples:

- Reset
- Continue as Guest
- Cancel

Style:

- Background: Surface
- Border: Primary
- Text: Primary
- Height: 52dp
- Radius: 12dp

### Text Button

Used for lightweight actions.

Examples:

- See all
- Manage
- Lookup via API
- Sign In

Style:

- Text color: Primary
- No background
- Font: 14–16px, 500

### Destructive Button

Used for delete actions.

Style:

- Text or background: Error
- Prefer confirmation dialog before destructive action

---

## Inputs

Used in Add/Edit Deck and Add/Edit Word screens.

### Default Input

Style:

- Background: Surface
- Border: Border
- Radius: 12px
- Height: 52dp minimum
- Text: On Surface
- Placeholder: On Surface Muted
- Padding: 16px horizontal

### Focused Input

Style:

- Border: Primary
- Optional soft primary background: Primary Soft

### Error Input

Style:

- Border: Error
- Error text below field
- Error icon optional

### Input Rules

Do:

- Label every field clearly.
- Mark required fields with `*`.
- Put helper text only when useful.
- Keep error messages short.

Don’t:

- Don’t use placeholder as the only label.
- Don’t make forms visually crowded.
- Don’t validate too aggressively while typing.

---

## Cards

### Stat Card

Used on Home and Progress screens.

Content:

- Icon
- Label
- Value

Style:

- Surface background
- Border
- 16px radius
- Compact layout
- Use semantic colors only for values

Example:

- Words Learned: dark value
- Due Today: orange value
- Accuracy: green value
- Streak: blue value

### Deck Card

Used in Deck List and Home preview.

Content:

- Icon
- Deck name
- Description/tag
- Word count
- Progress bar
- Percentage
- Optional menu

Style:

- Surface background
- Border
- 16px radius
- Icon container uses soft background
- Progress bar uses Primary
- Percentage uses Primary

### Word List Item

Used in Today’s Review and Vocabulary List.

Content:

- Word
- Part of speech
- Pronunciation
- Short meaning
- Review status
- Speaker icon

Style:

- Prefer list rows with thin dividers
- Use cards only when the list needs grouping
- Status dot + text is enough; avoid colorful full badges

### Flashcard

Used in Study Screen.

Content:

- Word
- Pronunciation
- Part of speech
- Meaning
- Example
- Speaker icon

Style:

- Large white card
- 20px radius
- 1px border
- Subtle shadow
- Generous spacing
- No decoration beyond content

---

## Chips

Used for filters such as:

- All
- IELTS
- Business
- Travel
- Due Today
- Learned

### Active Chip

- Background: Primary
- Text: White
- Radius: Full
- Height: 36–40dp

### Inactive Chip

- Background: Surface
- Border: Border
- Text: On Surface
- Radius: Full

### Status Chip Alternative

For due/learned states, prefer dot + text instead of a full chip.

Examples:

- Orange dot + Due Today
- Green dot + Learned
- Blue dot + Due in 2 days

---

## Bottom Navigation

Used on main app screens:

- Home
- Decks
- Study
- Stats
- Settings

### Active Item

- Icon: Primary
- Label: Primary
- Optional small indicator line above item

### Inactive Item

- Icon: Secondary
- Label: Secondary

### Rules

Do:

- Keep bottom navigation fixed on main screens.
- Hide bottom navigation on focused flows like Study and Add/Edit forms if needed.
- Use consistent icon style.

Don’t:

- Don’t show more than 5 bottom nav items.
- Don’t use filled icons for some items and outline icons for others unless active state requires it.

---

## Icons

Use outline icons with consistent stroke width.

Recommended icon style:

- Stroke: 2px
- Rounded line caps
- Simple geometric shapes
- No 3D icons
- No filled illustration-style icons

Suggested mappings:

- Home: house
- Decks: stacked layers/cards
- Study: play circle or graduation cap
- Stats: bar chart
- Settings: gear
- Word/audio: speaker
- Due: calendar
- Learned: check circle
- Accuracy: target
- Streak: flame
- Dictionary API: search or spark icon

---

## Screen Guidelines

## Onboarding Screen

Purpose:

- Introduce app
- Explain value quickly
- Let user start without friction

Required content:

- Brand name: MinLish Lite
- Tagline
- Minimal flashcard illustration
- Feature cards:
  - Flashcards
  - Spaced Repetition
  - Track Progress
- Get Started button
- Continue as Guest button
- Sign In link

Design rules:

- No bottom navigation
- Keep illustration simple
- Avoid gradients and large decorative backgrounds
- Keep CTA near lower part of screen

---

## Home Screen

Purpose:

- Show what the user should do today

Required content:

- App bar
- Greeting
- Summary stats
- Study Now CTA
- Today’s Review
- Your Decks preview
- Bottom navigation

Design rules:

- Study Now must be the dominant action.
- Today’s Review should be visible without excessive scrolling.
- Use orange only for urgent review states.
- Do not overload dashboard with charts.

---

## Deck List Screen

Purpose:

- Manage vocabulary decks

Required content:

- App bar
- Page title: Your Decks
- Search bar
- Filter chips
- New Deck action
- Deck list
- Bottom navigation

Design rules:

- Deck cards should be scannable.
- Progress should be shown with a thin bar.
- Keep menu action subtle.
- Avoid large colorful cards.

---

## Add / Edit Deck Screen

Purpose:

- Create or update a vocabulary deck

Required fields:

- Deck name
- Description
- Tag

Design rules:

- Use simple form layout.
- Keep Save as primary action.
- Validate required fields.
- Show error text below invalid fields.

---

## Vocabulary List Screen

Purpose:

- Show words inside a selected deck

Required content:

- Back button
- Deck title
- Deck summary
- Search bar
- Filter chips
- Word list
- Add Word FAB or button
- Bottom navigation may stay visible

Design rules:

- Word rows should be information-dense but readable.
- Use dividers instead of many separate heavy cards.
- Status should use dot + label.

---

## Add / Edit Word Screen

Purpose:

- Add or update a word

Required fields:

- Word
- Pronunciation
- Meaning
- Description
- Example
- Collocation
- Related words
- Note

Actions:

- Lookup via API
- Save Word
- Reset / Cancel

Design rules:

- Group fields in a Word Details card.
- Required fields: Word, Meaning.
- Dictionary API Preview should be secondary.
- Save button should be sticky or easy to reach.

---

## Word Detail Screen

Purpose:

- Read full word information

Required content:

- Word
- Pronunciation
- Part of speech
- Meaning
- Description
- Example
- Collocation
- Related words
- Note
- Review status
- Next review date
- Edit/Delete actions

Design rules:

- Prioritize readability.
- Use sections with clear labels.
- Avoid turning every field into a separate card.

---

## Study / Flashcard Screen

Purpose:

- Focused learning and review

Required content:

- Back button
- Deck name
- Progress count
- Progress bar
- Flashcard
- Flip Card action
- Again / Hard / Good / Easy buttons

Design rules:

- Hide bottom navigation to reduce distraction.
- Flashcard should dominate the screen.
- Use color only for review buttons.
- Keep text large and readable.
- Avoid unnecessary illustrations.

---

## Progress Screen

Purpose:

- Show learning analytics

Required content:

- Summary stats
- Weekly activity chart
- Retention rate
- Level estimate
- Recent achievements
- Bottom navigation

Design rules:

- Charts should be simple and readable.
- Do not use complex graph decorations.
- Use one primary chart only.
- Keep achievements as a list.

---

## Settings Screen

Purpose:

- Let user configure learning experience

Required content:

- Study Goal
- Daily Reminder
- Reminder Time
- Theme
- Account
- Support & About
- Bottom navigation

Design rules:

- Use grouped cards.
- Avoid too many toggles.
- Keep labels short.
- Use standard Android controls where possible.

---

## Interaction States

### Loading

Use for:

- API lookup
- Database loading
- Saving form

Style:

- Small CircularProgressIndicator
- Disable button while loading
- Optional text: “Loading…”

### Empty State

Use for:

- No decks
- No words
- No words due today

Style:

- Simple icon
- Short title
- Helpful message
- One action button

Example:

```text
No words yet
Add your first word to start learning.
[Add Word]
```

### Error State

Use for:

- API failure
- Required field missing
- Database action failed

Style:

- Error text in red
- Optional retry button
- Keep message human-readable

Example:

```text
Couldn’t fetch word details. Check your connection and try again.
```

### Success Feedback

Use for:

- Word saved
- Deck created
- Reminder updated

Style:

- Snackbar
- Short text

Example:

```text
Word saved successfully.
```

---

## Accessibility

### Contrast

- Text on background must meet at least 4.5:1 contrast where possible.
- Do not use light gray for important text.
- Primary blue should be readable on white.

### Touch Targets

Minimum touch target:

```text
48dp x 48dp
```

Applies to:

- Buttons
- Icons
- List item actions
- Bottom navigation items
- Chips

### Text

- Avoid text smaller than 12px.
- Use 14–16px for most reading content.
- Do not rely only on color to show status; include text labels.

Example:

Good:

```text
Orange dot + Due Today
```

Bad:

```text
Orange dot only
```

---

## Motion

Motion should be subtle and purposeful.

Recommended animations:

- Flashcard flip
- Button press state
- Screen transition
- Progress bar update
- Snackbar entrance

Avoid:

- Bouncy decorative animation
- Excessive gradient movement
- Animated illustrations
- Too many transitions on one screen

### Flashcard Flip

- Duration: 250–350ms
- Easing: FastOutSlowInEasing
- Must not make text unreadable
- Keep interaction predictable

---

## Data Visualization

### Progress Bars

Used for deck progress and study progress.

Style:

- Height: 4–6dp
- Track: Border or Surface Variant
- Fill: Primary
- Radius: Full

### Weekly Activity Chart

Style:

- Simple vertical bars
- Blue fill
- Light gridlines
- Minimal labels
- No 3D chart
- No excessive colors

### Retention Rate

Style:

- Circular progress or simple percentage card
- Green only if retention is good
- Add short explanation

---

## Form Validation

### Add Word

Required:

- Word
- Meaning

Optional:

- Pronunciation
- Description
- Example
- Collocation
- Related words
- Note

Validation messages:

- Word: “Word is required.”
- Meaning: “Meaning is required.”
- Word duplicate: “This word already exists in this deck.”
- API error: “Couldn’t fetch word details. Try again.”

### Add Deck

Required:

- Deck name

Optional:

- Description
- Tag

Validation messages:

- Deck name: “Deck name is required.”
- Duplicate deck: “A deck with this name already exists.”

---

## Do's and Don'ts

### Do

- Do keep the app mostly white and clean.
- Do use blue as the main action color.
- Do use cards only when they group meaningful content.
- Do keep forms spacious.
- Do use LazyColumn for long lists.
- Do show loading and error states for API lookup.
- Do make Study screen distraction-free.
- Do keep icons consistent and outline-based.
- Do use status text with status color.
- Do prioritize readability over decoration.

### Don’t

- Don’t use large blue-teal gradients on every screen.
- Don’t use 3D illustrations except a very subtle onboarding card stack.
- Don’t overload Home with too many charts.
- Don’t make every list item a colorful card.
- Don’t mix multiple icon styles.
- Don’t use saturated backgrounds behind large content areas.
- Don’t show more than one main CTA on a screen.
- Don’t put business logic inside composables.
- Don’t call API directly from UI.
- Don’t use color alone to communicate state.

---

## Jetpack Compose Implementation Notes

### Theme Tokens

Create tokens for:

```kotlin
object AppColors {
    val Primary = Color(0xFF2563EB)
    val PrimarySoft = Color(0xFFEFF6FF)
    val Secondary = Color(0xFF64748B)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F5F9)
    val Border = Color(0xFFE2E8F0)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceMuted = Color(0xFF64748B)
    val Success = Color(0xFF16A34A)
    val Warning = Color(0xFFF97316)
    val Medium = Color(0xFFEAB308)
    val Error = Color(0xFFEF4444)
}
```

### Recommended Components

Create reusable composables:

```kotlin
@Composable
fun PrimaryButton(...)

@Composable
fun SecondaryButton(...)

@Composable
fun AppTextField(...)

@Composable
fun SectionHeader(...)

@Composable
fun StatCard(...)

@Composable
fun DeckCard(...)

@Composable
fun WordListItem(...)

@Composable
fun Flashcard(...)

@Composable
fun BottomNavBar(...)
```

### State Rule

Composable should receive state and callbacks.

Good:

```kotlin
@Composable
fun DeckListScreen(
    state: DeckListUiState,
    onEvent: (DeckListEvent) -> Unit
)
```

Bad:

```kotlin
@Composable
fun DeckListScreen() {
    val dao = AppDatabase.getInstance().deckDao()
}
```

---

## Final Visual Direction

MinLish Lite should look like:

- A clean Android learning app
- Minimal and serious enough for study
- Friendly but not childish
- Modern but easy to implement
- Polished without unnecessary decoration

The best visual references are:

- Google Material 3
- Duolingo-level clarity, but less playful
- Notion-like whitespace
- Todoist-like simple productivity structure
- A real Android app rather than a concept shot
