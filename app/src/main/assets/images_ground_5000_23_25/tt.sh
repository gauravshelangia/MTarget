ext=".png"
for filename in *; do 
    [ -f "$filename" ] || continue
    
    echo $filename
    row=$(($filename/25))
    col=$(($filename%25))
    newname=${col}_${row}$ext
    echo $newname
    mv $filename $newname

done
