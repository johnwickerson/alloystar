ALS_FILE=$1
CMD=$2

if [ -z "$OS" ]
then
    OS="x86-mac"
else
    case $OS in
	x86-freebsd|x86-linux|x86-mac|x86-windows|amd64-linux);;
	*) exit 1;;
    esac
fi

if [ -z "$SOLVER" ]
then
    SOLVER="glucose"
else
    case $SOLVER in
	sat4j|cryptominisat|glucose|plingeling|lingeling|minisatprover|minisat);;
	*) exit 1;;
    esac
fi

if [ -z "$ITER" ]
then
    echo "Expected ITER to be true or false."
    exit 1
fi

export PATH=`pwd`/$OS:$PATH

java \
    -Djava.library.path="`pwd`/$OS" \
    -Dout=test.xml      `# output to test_<NUMBER>.xml`  \
    -Dsolver=$SOLVER    `# using given solver`           \
    -Dcmd=$CMD          `# run nth command in file`      \
    -Diter=$ITER        `# whether to run iteratively`   \
    edu/mit/csail/sdg/alloy4whole/RunAlloy               \
    $ALS_FILE
