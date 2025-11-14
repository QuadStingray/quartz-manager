import { useConfirm } from '#imports'

export function useConfirmation() {
  const confirm = useConfirm()
  const { showSuccessMessage, showInfoMessage } = useMessages()

  // eslint-disable-next-line unused-imports/no-unused-vars
  function doNothing() {}

    function confirmDelete(acceptCallback: () => void, rejectCallback: () => void = doNothing, acceptMessage: string = 'Action confirmed', acceptMessageDetail: string = acceptMessage, header: string = 'Are you sure', message: string = 'Should this entry be deleted ?') {
      confirm.require({
      message: message,
      header: header,
      icon: 'pi pi-info-circle',
      rejectLabel: 'Cancel',
      acceptLabel: 'Delete',
      rejectClass: 'p-button-secondary p-button-outlined',
      acceptClass: 'p-button-danger',
      accept: () => {
        acceptCallback()
        showSuccessMessage(acceptMessage, acceptMessageDetail)
      },
      reject: () => {
        rejectCallback()
        showInfoMessage('Action cancelled', 'No changes are processed')
      },
    })
  }

  function confirmAction(acceptCallback: () => void, acceptMessage: string = 'Action confirmed', acceptMessageDetail: string = acceptMessage, header: string = 'Attention', message: string = 'Should proceed with this action ?') {
    confirm.require({
      message,
      header,
      icon: 'pi pi-info-circle',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      rejectClass: 'p-button-secondary p-button-outlined',
      acceptClass: 'p-button-success',
      accept: () => {
        acceptCallback()
        showInfoMessage(acceptMessage, acceptMessageDetail)
      },
      reject: () => {
        showInfoMessage('Action cancelled')
      },
    })
  }

  return { confirmDelete, confirmAction }
}
