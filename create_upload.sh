[ ! -d "uploads" ] && mkdir uploads

cd src
pwd
zip $1.zip $1
mv $1.zip ../uploads
echo "Done"