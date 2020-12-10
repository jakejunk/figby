# Figby

Library for rendering ASCII text banners from FIGfonts.

## Rendering Fonts

```kotlin
// Font is "Roman", by Nick Miners (http://www.figlet.org/fonts/roman.flf)

val fontFile: InputStream = // FIGfont file
val font = FigFont.fromFile(fontFile)
val driver = FigDriver(font)

println(driver.convert("Figby"))
```

Output:

```
oooooooooooo  o8o              .o8                   
`888'     `8  `"'             "888                   
 888         oooo   .oooooooo  888oooo.  oooo    ooo 
 888oooo8    `888  888' `88b   d88' `88b  `88.  .8'  
 888    "     888  888   888   888   888   `88..8'   
 888          888  `88bod8P'   888   888    `888'    
o888o        o888o `8oooooo.   `Y8bod8P'     .8'     
                   d"     YD             .o..P'      
                   "Y88888P'             `Y8P'       
                                                     
```

## Printing Individual Characters

```kotlin
val font: FigFont = // Roman, same as above

println("'A':\n${font['A'.toInt()]}")
println("'9':\n${font['9'.toInt()]}")
```

Output:

```
'A':
      .o.      $
     .888.     $
    .8"888.    $
   .8' `888.   $
  .88ooo8888.  $
 .8'     `888. $
o88o     o8888o$
               $
               $
               $

'9':
 .ooooo.  $
888' `Y88.$
888    888$
 `Vbood888$
      888'$
    .88P' $
  .oP'    $
          $
          $
          $

```

## Fetching Font Metadata

```kotlin
val font: FigFont = // Roman, same as above

println("Hardblank: '${font.hardblank.toChar()}'")
println("Height: ${font.height} characters")
println("Baseline: ${font.baseline} characters")
println("Max width: ${font.maxLength} characters")
println("Print direction: ${font.printDirection}")
println("Horizontal layout: ${font.horizontalLayout}")
println("Vertical layout: ${font.verticalLayout}")
```

Output:

```
Hardblank: '$'
Height: 10 characters
Baseline: 10 characters
Max width: 30 characters
Print direction: LeftToRight
Horizontal layout: FullWidth
Vertical layout: FullHeight
```

A full example can be found in the `example` module [here](example/src/main/kotlin/main.kt).

## Font Resources

- [FIGfont database](http://www.figlet.org/fontdb.cgi)
- [Text to ASCII Art Generator](http://patorjk.com/software/taag/)

## TODO

- [ ] CI/CD
- [ ] Maven
- [ ] Compressed FIGfont parsing
- [ ] Multiline rendering
- [ ] Vertical fitting/smushing
- [ ] Word wrapping
