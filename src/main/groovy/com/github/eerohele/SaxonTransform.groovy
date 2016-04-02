import net.sf.saxon.Transform

class SaxonTransform extends Transform {
    static void main(String[] args) {
        new Transform().doTransform(args, '');
    }
}
