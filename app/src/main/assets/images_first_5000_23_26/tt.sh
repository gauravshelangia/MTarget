ext=".png"
for filename in *; do 
    [ -f "$filename" ] || continue
    
    echo $filename
    row=$(($filename/26))
    col=$(($filename%26))
    newname=${col}_${row}$ext
    echo $newname
    mv $filename $newname

done
