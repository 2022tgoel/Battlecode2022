# Usage: create_fork (src package name) (dest package name)

import sys
import os

_, src, dest = sys.argv

def check_name(dest):
	import re
	return dest == re.sub('[^a-zA-Z0-9]', '_', dest)

if not check_name(dest):
	print("Destination name contains invalid characters. Aborting.")
	exit()

src_folder = 'src/' + src
dest_folder = 'src/' + dest

if os.path.exists(dest_folder):
	print("Destination already exists: " + dest_folder + ". Aborting.")
	exit()

os.mkdir(dest_folder)

for file in os.listdir(src_folder):
	# lists files in the src folder, without the src folder name
	with open(os.path.join(src_folder, file)) as f:
		text = f.read()

	# replace the package name
	text = text.replace("package " + src, "package " + dest, 1)

	# save the destination file
	with open(os.path.join(dest_folder, file), 'w') as f:
		f.write(text)
		print("Saved file", os.path.join(dest_folder, file))

print("Completed")
