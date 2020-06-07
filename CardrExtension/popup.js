function onResponse(response) {
  console.log(`Received ${response}`);
}

function onError(error) {
  console.log(`Error: ${error}`);
}

function openOCRTool() {
  chrome.runtime.sendMessage("openOCRTool");  
}

function openCardrFromBtn() {
  chrome.runtime.sendMessage("openCardrFromBtn");
}

document.addEventListener('DOMContentLoaded', function() {
  var open = document.getElementById('openCardr');
  open.addEventListener('click', function() {
    open.innerHTML = "Launching Cardr...";
    open.disabled = true;
    open.style.backgroundColor = '#bbb';
    openCardrFromBtn();
  });

  var ocr = document.getElementById('useOCR');
  ocr.addEventListener('click', function() {
    ocr.innerHTML = "Launching Cardr...";
    ocr.disabled = true;
    ocr.style.backgroundColor = '#bbb';
    openOCRTool();
  });

  chrome.runtime.onMessage.addListener(function(request, sender, sendResponse) {
    if (request == "closeCardrPopup") {
      window.setTimeout(
        function() { window.close(); },
        5000
      );
    }
  });
});
