#!/bin/bash
POS=`pwd`
REPOS=repos
WC1=wc1
WC2=wc2
#
URL=file://$POS/$REPOS
#
# Remove any existing repository, may be from a previous run
rm -fr $POS/$REPOS
# Removed any existing working copies
rm -fr $POS/$WC1
rm -fr $POS/$WC2
svnadmin create $POS/$REPOS
#
svn mkdir --parents $URL/project1 \
	$URL/project1/trunk \
	$URL/project1/tags \
	$URL/project1/branches \
	-m"- Create structure in repository"

# Create a real working copy

# Create a working copy
#
svn co $URL/project1/trunk $WC1
cd $WC1
echo "Content1" >f1.txt
echo "Content1" >f2.txt
svn add f1.txt
svn add f2.txt
svn ci -m"- First changes"
cd $POS
#
#
# Create a first Tag
svn cp $URL/project1/trunk $URL/project1/tags/RELEASE-0.0.1 -m"- First Tag for Release 0.0.1"



svnadmin dump $POS/$REPOS >$REPOS.dump
