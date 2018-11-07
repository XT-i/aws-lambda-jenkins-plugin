function updateAwsLambdaOptionalBlock(c,scroll) {
    // find the start TR
    var s = $(c);
    while(!s.hasClassName("awslambda-optional-block-start"))
        s = s.up();

    // find the beginning of the rowvg
    var vg =s;
    while (!vg.hasClassName("rowvg-start"))
        vg = vg.next();

    var checked = xor(c.checked,Element.hasClassName(c.parentElement.parentElement,"negative"));

    vg.rowVisibilityGroup.makeInnerVisible(checked);

    if (checked && scroll) {
        var D = YAHOO.util.Dom;

        var r = D.getRegion(s);
        r = r.union(D.getRegion(vg.rowVisibilityGroup.end));
        scrollIntoView(r);
    }

    if (c.name == 'hudson-tools-InstallSourceProperty') {
        // Hack to hide tool home when "Install automatically" is checked.
        var homeField = findPreviousFormItem(c, 'home');
        if (homeField != null && homeField.value == '') {
            var tr = findAncestor(homeField, 'TR');
            if (tr != null) {
                tr.style.display = c.checked ? 'none' : '';
                layoutUpdateCallback.call();
            }
        }
    }
}

document.addEventListener("DOMContentLoaded", function() {
    Behaviour.specify("TR.awslambda-optional-block-start", "awslambda-optional-block-start", 100, function(e) {
        // set start.ref to checkbox in preparation of row-set-end processing
        var checkbox = e.down().down();
        e.setAttribute("ref", checkbox.id = "cb"+(iota++));
    });

    Behaviour.specify("TR.awslambda-optional-block-start ", "awslambda-optional-block-start ", 100, function(e) { // see optionalBlock.jelly
        // this is suffixed by a pointless string so that two processing for optional-block-start
        // can sandwitch row-set-end
        // this requires "TR.row-set-end" to mark rows
        var checkbox = e.down().down();
        updateAwsLambdaOptionalBlock(checkbox,false);
    });
});
