ext=".png"
for filename in *; do 
    [ -f "$filename" ] || continue
    
    echo $filename
    row=$(($filename/22))
    col=$(($filename%22))
    newname=${col}_${row}$ext
    echo $newname
    mv $filename $newname

done
