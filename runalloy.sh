# set this to one of:
#   x86-freebsd, x86-linux, x86-mac, x86-windows, amd64-linux
OS="x86-mac"

ALS_FILE=$@

# set this to one of:
#  sat4j, cryptominisat, glucose, plingeling, lingeling,
#  minisatprover, minisat
SOLVER="glucose"

export PATH=`pwd`/$OS:$PATH

java \
    -Djava.library.path="`pwd`/$OS" \
    -Dout=test.xml      `# output to test_<NUMBER>.xml`        \
    -Dsolver=$SOLVER    `# using given solver`                 \
    -Dcmd=0             `# run first command in file`          \
    -Diter=true         `# run iteratively`                    \
    edu/mit/csail/sdg/alloy4whole/RunAlloy                     \
    $ALS_FILE
